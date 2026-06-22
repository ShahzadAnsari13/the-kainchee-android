package com.thekainchee.user.presentation.payment.state

sealed class PaymentCallbackEvent {
    data class Success(
        val paymentId: String,
        val orderId: String,
        val signature: String
    ) : PaymentCallbackEvent()

    data class Error(
        val message: String
    ) : PaymentCallbackEvent()
}
