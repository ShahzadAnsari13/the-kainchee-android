package com.thekainchee.user.data.remote.dto.auth

data class VerifyOtpResponseDto(
    val message :String,
    val accessToken : String,
    val refreshToken : String
)