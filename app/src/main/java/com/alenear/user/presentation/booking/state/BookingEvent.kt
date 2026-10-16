package com.alenear.user.presentation.booking.state

import com.alenear.user.presentation.booking.model.BookingUiModel

sealed class BookingEvent {
    data class OpenPaymentSheet(
        val booking: BookingUiModel
    ) : BookingEvent()
}