package com.alenear.user.presentation.payment.state

sealed class OnlinePaymentEvent {
    data class OpenRazorpay(
        val orderId: String,
        val amount: Long,
        val currency: String
    ) : OnlinePaymentEvent()

    data class PaymentError(val message: String) : OnlinePaymentEvent()
    data class PaymentFailed(val message: String) : OnlinePaymentEvent()
    object NavigateToSuccess : OnlinePaymentEvent()
    object NavigateToVerification : OnlinePaymentEvent()
}