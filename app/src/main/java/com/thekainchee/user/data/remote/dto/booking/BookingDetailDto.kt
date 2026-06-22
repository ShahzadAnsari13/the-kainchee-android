package com.thekainchee.user.data.remote.dto.booking

data class BookingDetailDto(
    val _id: String,
    val parlourId: ParlourDataDto,
    val staffId: ParlourStaffDto,
    val services: List<BookingDetailsServiceDto>,
    val totalPrice: Double,
    val bookingDate: String,
    val slotStartTime: String,
    val slotEndTime: String,
    val paymentMethod: String?,
    val paymentStatus: String,
    val bookingStatus: String,
    val createdAt : String,
)
