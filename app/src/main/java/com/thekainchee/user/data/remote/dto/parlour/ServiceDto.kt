package com.thekainchee.user.data.remote.dto.parlour

data class ServiceDto(
    val serviceName : String,
    val bookingCount : Int,
    val avgPrice : Double,
    val avgDuration : Double
)
