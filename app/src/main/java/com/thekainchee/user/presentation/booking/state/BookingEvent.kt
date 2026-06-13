package com.thekainchee.user.presentation.booking.state

import com.thekainchee.user.presentation.booking.model.BookingUiModel

sealed interface BookingEvent {
    data class OpenPaymentSheet(
        val booking: BookingUiModel
    ) : BookingEvent
}