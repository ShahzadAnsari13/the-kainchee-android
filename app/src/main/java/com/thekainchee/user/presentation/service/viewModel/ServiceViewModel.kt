package com.thekainchee.user.presentation.service.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.service.state.ServiceCategoryState
import com.thekainchee.user.presentation.service.state.ServiceListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun addService(serviceId: String){

        viewModelScope.launch {

            repository.insert(serviceId)
            loadSelectedServices()
        }
    }
    fun removeService(serviceId: String){

        viewModelScope.launch {

            repository.remove(serviceId)
            loadSelectedServices()
        }
    }
    fun loadSelectedServices(){

        viewModelScope.launch {

            _selectedServiceIds.value =
                repository.getAll()
                    .map { it.serviceId }
        }
    }
    fun clearAllServices(){

        viewModelScope.launch {

            repository.clearAll()

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

    fun getServicesByCategory(parlourId : String, categoryId : String) {
        if(
            lastParlourId == parlourId &&
            lastCategoryId == categoryId &&
            _serviceListState.value is ServiceListState.Success
        ){
            return
        }
        lastParlourId = parlourId
        lastCategoryId = categoryId
        _serviceListState.value = ServiceListState.Loading
        viewModelScope.launch {
            val result = repository.getServicesByCategory(parlourId, categoryId)
            if (result.isSuccess) {
                val data = result.getOrNull().orEmpty()
                if (data.isNotEmpty()) {
                    val selectedIds = repository
                        .getAll()
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