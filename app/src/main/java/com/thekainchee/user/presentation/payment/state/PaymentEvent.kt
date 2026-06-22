package com.thekainchee.user.presentation.payment.state

sealed class PaymentEvent {
    data object NavigateToSuccess :
        PaymentEvent()
    data class Message(val msg: String) : PaymentEvent()
}