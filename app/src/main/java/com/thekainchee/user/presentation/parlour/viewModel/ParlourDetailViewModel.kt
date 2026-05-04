package com.thekainchee.user.presentation.parlour.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ParlourRepository
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.service.state.ServiceCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.orEmpty

@HiltViewModel
class ParlourDetailViewModel @Inject constructor(private val repository: ParlourRepository,private val serviceRepository: ServiceRepository) : ViewModel()  {

    private val _parlourDetailedState = MutableStateFlow<ParlourDetailedState>(ParlourDetailedState.Idle)
    val parlourDetailedState : StateFlow<ParlourDetailedState> = _parlourDetailedState


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


}