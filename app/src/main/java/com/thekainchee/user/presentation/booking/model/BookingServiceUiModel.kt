package com.thekainchee.user.presentation.booking.model

data class BookingServiceUiModel(
    val serviceId: String,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val image: String?
)