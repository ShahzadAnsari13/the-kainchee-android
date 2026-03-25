package com.thekainchee.user.data.remote.dto.auth

data class RequestOtpDto(
    val countryCode : String = "+91",
    val phone : String
)