package com.thekainchee.user.data.remote.dto.booking

data class MyBookingsResponseDto(
    val success: Boolean,
    val count: Int,
    val bookings: List<MyBookingDto>
)
