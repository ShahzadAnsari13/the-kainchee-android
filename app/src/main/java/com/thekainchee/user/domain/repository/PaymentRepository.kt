package com.thekainchee.user.domain.repository

import com.thekainchee.user.data.remote.dto.payment.CreateOrderResponseDto
import com.thekainchee.user.data.remote.dto.payment.OnlinePaymentResponseDto
import com.thekainchee.user.data.remote.dto.payment.PaymentResponseDto
import com.thekainchee.user.presentation.payment.model.VerifyPaymentParams

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