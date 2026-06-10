package com.thekainchee.user.presentation.booking.model

data class CreateBookingParams(
    val parlourId: String,
    val staffId: String,
    val bookingDate: String,
    val serviceIds: List<String>,
    val slotStartTime: String
)