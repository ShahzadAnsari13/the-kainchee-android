package com.alenear.user.domain.repository

import com.alenear.user.data.remote.dto.auth.CommonMessageDto

interface AuthRepository {
    suspend fun requestOtp(countryCode: String, phone: String): Result<CommonMessageDto>
    suspend fun verifyOtp(countryCode: String, phone: String, otp: String):Result<Boolean>

    suspend fun logout():Result<Unit>
}


