package com.alenear.user.presentation.booking.state

import com.alenear.user.presentation.booking.model.MyBookingUiModel

sealed class MyBookingsUiState {
    object Idle : MyBookingsUiState()

    object Loading : MyBookingsUiState()

    data class Success(
        val bookings: List<MyBookingUiModel>
    ) : MyBookingsUiState()

    object Empty : MyBookingsUiState()

    data class Error(
        val message: String
    ) : MyBookingsUiState()
}