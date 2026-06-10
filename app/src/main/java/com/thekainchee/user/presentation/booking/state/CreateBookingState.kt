package com.thekainchee.user.presentation.booking.state

import com.thekainchee.user.presentation.booking.model.BookingUiModel

sealed class CreateBookingState {
    object Idle : CreateBookingState()
    object Loading : CreateBookingState()
    data class Success(val booking: BookingUiModel) : CreateBookingState()
    data class Error(val message: String) : CreateBookingState()

}