package com.thekainchee.user.data.remote.dto.payment

data class CreateOrderResponseDto(
    val orderId: String,
    val amount: Long,
    val currency: String
)
