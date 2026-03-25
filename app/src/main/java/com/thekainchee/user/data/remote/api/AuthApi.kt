package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.auth.CommonMessageDto
import com.thekainchee.user.data.remote.dto.auth.RefreshTokenRequestDto
import com.thekainchee.user.data.remote.dto.auth.RefreshTokenResponseDto
import com.thekainchee.user.data.remote.dto.auth.RequestOtpDto
import com.thekainchee.user.data.remote.dto.auth.VerifyOtpDto
import com.thekainchee.user.data.remote.dto.auth.VerifyOtpResponseDto
import okhttp3.Request
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/request-otp")
    suspend fun requestOtp(
        @Body request: RequestOtpDto
    ): Response<CommonMessageDto>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpDto
    ) : Response<VerifyOtpResponseDto>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): Response<RefreshTokenResponseDto>
}