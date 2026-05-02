package com.thekainchee.user.presentation.dashboard.home.model

import kotlin.time.Duration

data class ServiceUI(
    val serviceName :String,
    val bookingCount : Int,
    val avgPrice : Double,
    val avgDuration: Double
)