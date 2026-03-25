package com.thekainchee.user.data.remote.dto.auth

data class VerifyOtpDto(
    val countryCode : String = "+91",
    val phone : String,
    val otp : String
)