package com.thekainchee.user.domain.repository

import com.thekainchee.user.data.remote.dto.auth.CommonMessageDto
import com.thekainchee.user.data.remote.dto.auth.VerifyOtpResponseDto

interface AuthRepository {
    suspend fun requestOtp(countryCode: String, phone: String): Result<CommonMessageDto>
    suspend fun verifyOtp(countryCode: String, phone: String, otp: String): Result<VerifyOtpResponseDto>
}


