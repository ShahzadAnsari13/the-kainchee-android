package com.thekainchee.user.presentation.payment.state

sealed class OnlinePaymentEvent {
    data class OpenRazorpay(
        val orderId: String,
        val amount: Long,
        val currency: String
    ) : OnlinePaymentEvent()

    data class Message(
        val message: String
    ) : OnlinePaymentEvent()

    object NavigateToSuccess : OnlinePaymentEvent()
}