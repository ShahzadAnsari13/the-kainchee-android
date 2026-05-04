package com.thekainchee.user.presentation.service.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.presentation.service.state.ServiceCategoryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.orEmpty

class ServiceViewModel @Inject constructor(private val repository: ServiceRepository) : ViewModel() {
    private val _serviceCategoryState = MutableStateFlow<ServiceCategoryState>(ServiceCategoryState.Idle)
    val serviceCategoryState : StateFlow<ServiceCategoryState> = _serviceCategoryState

    fun getServiceCategories(id : String){
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

}