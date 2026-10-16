package com.alenear.user.presentation.payment.model

data class VerifyPaymentParams(
    val orderId: String,
    val paymentId: String,
    val signature: String
)
