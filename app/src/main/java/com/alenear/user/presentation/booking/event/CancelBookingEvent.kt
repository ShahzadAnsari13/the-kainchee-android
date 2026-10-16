package com.alenear.user.presentation.booking.event

sealed class CancelBookingEvent {
    data class Success(val message: String) : CancelBookingEvent()
    data class Error(val message: String) : CancelBookingEvent()
}