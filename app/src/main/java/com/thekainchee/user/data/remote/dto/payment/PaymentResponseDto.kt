package com.thekainchee.user.data.remote.dto.payment

data class PaymentResponseDto(val success: Boolean,
                              val bookingId: String,
                              val paymentMethod: String,
                              val paymentStatus: String)
