package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.payment.CreateOrderResponseDto
import com.thekainchee.user.data.remote.dto.payment.OnlinePaymentResponseDto
import com.thekainchee.user.data.remote.dto.payment.PaymentResponseDto
import com.thekainchee.user.data.remote.dto.payment.VerifyPaymentRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {
    @POST("payment/booking/{bookingId}/pay-with-wallet")
    suspend fun payWithWallet(
        @Path("bookingId") bookingId: String
    ): Response<PaymentResponseDto>

    @POST("payment/booking/{bookingId}/pay-with-cash")
    suspend fun payWithCash(
        @Path("bookingId") bookingId: String
    ): Response<PaymentResponseDto>

    @POST("payment/booking/{bookingId}/create-order")
    suspend fun createOrder(
        @Path("bookingId") bookingId: String
    ): Response<CreateOrderResponseDto>

    @POST("payment/booking/verify")
    suspend fun verifyPayment(
        @Body request: VerifyPaymentRequestDto
    ): Response<OnlinePaymentResponseDto>
}