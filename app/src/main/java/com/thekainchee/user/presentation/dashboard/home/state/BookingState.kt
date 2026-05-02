package com.thekainchee.user.presentation.dashboard.home.state

import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI

sealed class BookingState {

    object Idle : BookingState()

    object Loading : BookingState()

    data class Success( val data : List<BookingUI>) : BookingState()

    data class Error(val message : String) : BookingState()
}