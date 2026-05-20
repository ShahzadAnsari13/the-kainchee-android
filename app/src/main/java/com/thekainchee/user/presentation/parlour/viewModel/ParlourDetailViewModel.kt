package com.thekainchee.user.presentation.parlour.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ParlourRepository
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.parlour.state.ParlourEvent
import com.thekainchee.user.presentation.service.state.ServiceCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.orEmpty

@HiltViewModel
class ParlourDetailViewModel @Inject constructor(private val repository: ParlourRepository,private val serviceRepository: ServiceRepository) : ViewModel()  {

    private val _parlourDetailedState = MutableStateFlow<ParlourDetailedState>(ParlourDetailedState.Idle)
    val parlourDetailedState : StateFlow<ParlourDetailedState> = _parlourDetailedState

    private val _event = MutableSharedFlow<ParlourEvent>()

    val event = _event.asSharedFlow()
    private var loadedParlourId: String? = null

    fun checkParlourStatus(parlourId : String){
        viewModelScope.launch {
            val result = repository.checkParlourStatus(parlourId)
            result.onSuccess { isOpenNow ->
                if(isOpenNow){
                    _event.emit(ParlourEvent.NavigateToServices)
                }else{
                    _event.emit(ParlourEvent.ParlourClosed)
                }
            }.onFailure { error ->
                _event.emit(
                    ParlourEvent.ShowError(
                        error.message ?: "Failed to check Parlour Status"
                    )
                )
            }

        }
    }
    fun getParlourDetails(id : String){

        if (
            loadedParlourId == id &&
            _parlourDetailedState.value is ParlourDetailedState.Success
        ) {
            return
        }

        loadedParlourId = id
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