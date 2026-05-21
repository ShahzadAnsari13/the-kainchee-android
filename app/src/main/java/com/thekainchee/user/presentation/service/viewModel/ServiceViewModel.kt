package com.thekainchee.user.presentation.service.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.presentation.service.state.BookingPreviewEvent
import com.thekainchee.user.presentation.service.state.ServiceCategoryState
import com.thekainchee.user.presentation.service.state.ServiceListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.orEmpty
@HiltViewModel
class ServiceViewModel @Inject constructor(private val repository: ServiceRepository) : ViewModel() {
    private val _serviceCategoryState = MutableStateFlow<ServiceCategoryState>(ServiceCategoryState.Idle)
    val serviceCategoryState : StateFlow<ServiceCategoryState> = _serviceCategoryState

    private val _serviceListState = MutableStateFlow<ServiceListState>(ServiceListState.Idle)
    val serviceListState : StateFlow<ServiceListState> = _serviceListState
    private var loadedParlourId: String? = null
    private var lastCategoryId: String? = null
    private var lastParlourId: String? = null


    private val _selectedServiceIds = MutableStateFlow<List<String>>(emptyList())

    val selectedServiceIds: StateFlow<List<String>> = _selectedServiceIds

    private val _bookingPreviewEvent = MutableSharedFlow<BookingPreviewEvent>()
    var bookingPreviewEvent : SharedFlow<BookingPreviewEvent> = _bookingPreviewEvent


    fun getBookingPreview(parlourId: String,serviceIds : List<String>){
        viewModelScope.launch {
            val result = repository.getBookingPreview(parlourId,serviceIds)
            if(result.isSuccess){
                val data = result.getOrNull()
                if(!data?.services.isNullOrEmpty()){
                    _bookingPreviewEvent.emit(BookingPreviewEvent.OpenBottomSheet(data))
                }else{
                    _bookingPreviewEvent.emit(BookingPreviewEvent.ShowToast("No services selected"))
                }
            }else{
                _bookingPreviewEvent.emit(BookingPreviewEvent.ShowToast(result.exceptionOrNull()?.message ?: "Failed to load booking preview"))
            }
        }
    }
    fun addService(parlourId: String,serviceId: String){

        viewModelScope.launch {

            repository.insert(parlourId,serviceId)
            loadSelectedServices(parlourId)
        }
    }
    fun removeService(parlourId: String,serviceId: String){

        viewModelScope.launch {

            repository.remove(parlourId,serviceId)
            loadSelectedServices(parlourId)
        }
    }
    fun loadSelectedServices(parlourId: String){

        viewModelScope.launch {

            _selectedServiceIds.value =
                repository.getAll(parlourId)
                    .map { it.serviceId }
        }
    }
    fun clearAllServices(parlourId: String){

        viewModelScope.launch {

            repository.clearAll(parlourId)

            _selectedServiceIds.value = emptyList()
        }
    }
    fun getServiceCategories(id : String){
        if (
            loadedParlourId == id &&
            _serviceCategoryState.value is ServiceCategoryState.Success
        ) {
            return
        }

        loadedParlourId = id
        _serviceCategoryState.value = ServiceCategoryState.Loading
        viewModelScope.launch {
            val result = repository.getServiceCategories(id)
            if (result.isSuccess){
                val data = result.getOrNull().orEmpty()
                if(data.isNotEmpty()){
                    _serviceCategoryState.value = ServiceCategoryState.Success(data)
                }else{
                    _serviceCategoryState.value = ServiceCategoryState.Empty
                }
            }else{
                _serviceCategoryState.value = ServiceCategoryState.Error(result.exceptionOrNull()?.message ?: "Failed to load service categories")
            }

        }
    }

    fun getServicesByCategory(force : Boolean  = false, parlourId : String, categoryId : String) {
        if(
            !force && lastParlourId == parlourId &&
            lastCategoryId == categoryId &&
            _serviceListState.value is ServiceListState.Success
        ){
            return
        }
        Log.d("API_CHECK","CALLED REPOSITORY")
        lastParlourId = parlourId
        lastCategoryId = categoryId
        _serviceListState.value = ServiceListState.Loading
        viewModelScope.launch {
            val result = repository.getServicesByCategory(parlourId, categoryId)
            if (result.isSuccess) {
                val data = result.getOrNull().orEmpty()
                if (data.isNotEmpty()) {
                    val selectedIds = repository
                        .getAll(parlourId)
                        .map { it.serviceId }

                    val updatedServices = data.map {

                        it.copy(
                            isAdded = selectedIds.contains(it.id)
                        )
                    }
                    _serviceListState.value = ServiceListState.Success(updatedServices)
                } else {
                    _serviceListState.value = ServiceListState.Empty
                }

            }else{
                _serviceListState.value = ServiceListState.Error(result.exceptionOrNull()?.message ?: "Failed to load services")

            }


        }
    }
}