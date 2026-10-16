package com.alenear.user.presentation.dashboard.model

data class ServiceUI(
    val serviceName :String,
    val bookingCount : Int,
    val avgPrice : Double,
    val avgDuration: Double,
    val image: String
)