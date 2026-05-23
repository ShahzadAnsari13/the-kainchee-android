package com.thekainchee.user.presentation.booking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.BookingRepository
import com.thekainchee.user.presentation.booking.state.SlotState
import com.thekainchee.user.presentation.booking.state.StaffState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class BookingViewModel @Inject constructor(val repository: BookingRepository) : ViewModel() {

    private val _staffState = MutableStateFlow<StaffState>(StaffState.Idle)
    val staffState: StateFlow<StaffState> = _staffState

    private val  _slotState = MutableStateFlow<SlotState> (SlotState.Idle)
    val slotState : StateFlow<SlotState> = _slotState

    fun getParlourStaffs(parlourId : String){

        _staffState.value = StaffState.Loading
        viewModelScope.launch {
            val result = repository.getParlourStaffs(parlourId)
            if(result.isSuccess){
                val data = result.getOrNull().orEmpty()

                if (data.isNotEmpty()) {

                    _staffState.value = StaffState.Success(data)

                } else {

                    _staffState.value = StaffState.Empty
                }

            }else{
                _staffState.value = StaffState.Error(result.exceptionOrNull()?.message ?: "Failed to load staffs")
            }
        }

    }

    fun getStaffSlots(parlourId: String, staffId: String, date: String){
        viewModelScope.launch {
            val result = repository.getStaffSlots(parlourId, staffId, date)
            if(result.isSuccess){
                val data = result.getOrNull().orEmpty()
                if (data.isNotEmpty()) {
                    _slotState.value = SlotState.Success(data)
                } else {
                    _slotState.value = SlotState.Empty
                }
            }else{
                _slotState.value = SlotState.Error(result.exceptionOrNull()?.message ?: "Failed to load slots")
            }
        }
    }

}