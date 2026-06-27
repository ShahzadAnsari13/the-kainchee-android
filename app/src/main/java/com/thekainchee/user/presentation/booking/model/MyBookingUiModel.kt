package com.thekainchee.user.presentation.booking.model

data class MyBookingUiModel(
    val bookingId: String,
    val parlourName: String,
    val staffName: String,
    val bookingDate: String,
    val slotStartTime: String,
    val bookingStatus: String,
    val totalPrice: Double,
    val serviceName: String,
    val serviceCount: Int
)