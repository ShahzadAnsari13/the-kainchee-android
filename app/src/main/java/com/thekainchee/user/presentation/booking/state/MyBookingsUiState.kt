package com.thekainchee.user.presentation.booking.state

import com.thekainchee.user.presentation.booking.model.MyBookingUiModel

sealed class MyBookingsUiState {

    object Loading : MyBookingsUiState()

    data class Success(
        val bookings: List<MyBookingUiModel>
    ) : MyBookingsUiState()

    object Empty : MyBookingsUiState()

    data class Error(
        val message: String
    ) : MyBookingsUiState()
}