package com.thekainchee.user.data.remote.dto.booking

data class BookingRequestDto(val parlourId: String, val staffId: String,
    val bookingDate: String, val serviceIds: List<String>,val slotStartTime: String)