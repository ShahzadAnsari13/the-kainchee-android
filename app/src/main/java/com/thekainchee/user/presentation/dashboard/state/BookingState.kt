package com.thekainchee.user.presentation.dashboard.state

import com.thekainchee.user.presentation.dashboard.model.BookingUI

sealed class BookingState {

    object Idle : BookingState()

    object Loading : BookingState()

    data class Success( val data : List<BookingUI>) : BookingState()

    data class Error(val message : String) : BookingState()
}