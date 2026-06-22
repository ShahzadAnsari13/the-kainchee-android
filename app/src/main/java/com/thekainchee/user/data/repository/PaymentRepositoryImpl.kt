package com.thekainchee.user.data.repository

import android.util.Log
import com.thekainchee.user.data.remote.api.PaymentApi
import com.thekainchee.user.data.remote.dto.payment.CreateOrderResponseDto
import com.thekainchee.user.data.remote.dto.payment.OnlinePaymentResponseDto
import com.thekainchee.user.data.remote.dto.payment.PaymentResponseDto
import com.thekainchee.user.data.remote.dto.payment.VerifyPaymentRequestDto
import com.thekainchee.user.domain.repository.PaymentRepository
import com.thekainchee.user.presentation.payment.model.VerifyPaymentParams
import com.thekainchee.user.utils.ErrorUtils
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(private val api : PaymentApi) : PaymentRepository {
    override suspend fun payWithWallet(bookingId: String): Result<Boolean> {
        return try{
            val response  = api.payWithWallet(bookingId)
            if (response.isSuccessful &&
                response.body()?.success == true
            ) {
                Result.success(true)
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(
                    Exception(error.message ?: "Wallet payment failed")
                )
            }
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun payWithCash(bookingId: String): Result<Boolean> {
        return try{
            val response  = api.payWithCash(bookingId)
            if (response.isSuccessful &&
                response.body()?.success == true
            ) {
                Result.success(true)
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(
                    Exception(error.message ?: "Cash payment failed")
                )
            }

        }catch(e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun createOrder(
        bookingId: String
    ): Result<CreateOrderResponseDto> {
        return try {
            val response = api.createOrder(bookingId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("Response body is null")
                    )
                }
            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Something went wrong"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyPayment(
        params: VerifyPaymentParams
    ): Result<OnlinePaymentResponseDto> {

        return try {

            val request = VerifyPaymentRequestDto(
                razorpay_order_id = params.orderId,
                razorpay_payment_id = params.paymentId,
                razorpay_signature = params.signature
            )

            val response = api.verifyPayment(request)

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("Response body is null")
                    )
                }

            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string()
                            ?: "Payment verification failed"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}