package com.alenear.user.presentation.payment.state

sealed class PaymentEvent {
    data object NavigateToSuccess :
        PaymentEvent()
    data class Message(val msg: String) : PaymentEvent()
}