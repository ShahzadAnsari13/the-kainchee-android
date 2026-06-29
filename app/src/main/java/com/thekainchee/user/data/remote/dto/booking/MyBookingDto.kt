package com.thekainchee.user.data.remote.dto.booking

data class MyBookingDto(
    val bookingId: String,
    val parlourName: String,
    val staffName: String,
    val bookingDate: String,
    val slotStartTime: String,
    val bookingStatus: String,
    val totalPrice: Double,
    val serviceName: String,
    val serviceCount: Int,
    val serviceImage: String
)
