package com.thekainchee.user.presentation.booking.state

import com.thekainchee.user.presentation.booking.model.BookingUiModel

sealed class BookingEvent {
    data class OpenPaymentSheet(
        val booking: BookingUiModel
    ) : BookingEvent()
}