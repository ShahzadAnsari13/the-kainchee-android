package com.alenear.user.domain.repository

import com.alenear.user.data.remote.dto.payment.CreateOrderResponseDto
import com.alenear.user.data.remote.dto.payment.OnlinePaymentResponseDto
import com.alenear.user.presentation.payment.model.VerifyPaymentParams

interface PaymentRepository {
    suspend fun payWithWallet(bookingId: String): Result<Boolean>

    suspend fun payWithCash(bookingId: String): Result<Boolean>

    suspend fun createOrder(
        bookingId: String
    ): Result<CreateOrderResponseDto>

    suspend fun verifyPayment(
        params: VerifyPaymentParams
    ): Result<OnlinePaymentResponseDto>
}