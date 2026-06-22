package com.thekainchee.user.presentation.booking.model

import com.thekainchee.user.data.remote.dto.address.LocationDto

data class BookingDetailUiModel(
    val bookingId: String,
    val parlourName: String,
    val parlourPhone: String?,
    val staffName: String,
    val services: List<BookingServiceUiModel>,
    val totalPrice: Double,
    val bookingDate: String,
    val slotStartTime: String,
    val slotEndTime: String,
    val paymentMethod: String?,
    val paymentStatus: String,
    val bookingStatus: String,
    val location: LocationDto?,
    val createdAt : String
)