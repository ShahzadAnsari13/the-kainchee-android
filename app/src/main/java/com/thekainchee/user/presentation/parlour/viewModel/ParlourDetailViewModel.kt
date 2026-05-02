package com.thekainchee.user.presentation.parlour.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ParlourRepository
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.parlour.state.ServiceCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.orEmpty

@HiltViewModel
class ParlourDetailViewModel @Inject constructor(private val repository: ParlourRepository) : ViewModel()  {

    private val _parlourDetailedState = MutableStateFlow<ParlourDetailedState>(ParlourDetailedState.Idle)
    val parlourDetailedState : StateFlow<ParlourDetailedState> = _parlourDetailedState

    private val _serviceCategoryState = MutableStateFlow<ServiceCategoryState>(ServiceCategoryState.Idle)
    val serviceCategoryState : StateFlow<ServiceCategoryState> = _serviceCategoryState
    fun getParlourDetails(id : String){
        _parlourDetailedState.value = ParlourDetailedState.Loading
        viewModelScope.launch {

            val result = repository.getParlourDetails(id)

            result.onSuccess { data ->
                _parlourDetailedState.value = ParlourDetailedState.Success(data)
            }.onFailure { error ->
                _parlourDetailedState.value =
                    ParlourDetailedState.Error(
                        error.message ?: "Failed to load Parlour Details"
                    )
            }

        }
    }

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