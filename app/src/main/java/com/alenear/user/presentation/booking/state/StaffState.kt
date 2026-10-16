package com.alenear.user.presentation.booking.state

import com.alenear.user.presentation.booking.model.StaffUiModel

sealed class StaffState {

    object  Idle : StaffState()
    object Loading : StaffState()
    data class Success(val data : List<StaffUiModel>) : StaffState()
    object Empty : StaffState()
    data class Error(val message : String) : StaffState()
}