package com.alenear.user.presentation.booking.state

import com.alenear.user.presentation.booking.model.SlotUiModel

sealed class SlotState {
    object  Idle : SlotState()
    object Loading : SlotState()
    data class Success(val data : List<SlotUiModel>) : SlotState()
    object Empty : SlotState()
    data class Error(val message : String) : SlotState()
}