package com.thekainchee.user.data.remote.dto.payment

data class VerifyPaymentRequestDto(
    val razorpay_order_id: String,
    val razorpay_payment_id: String,
    val razorpay_signature: String
)
