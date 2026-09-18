package com.thekainchee.user.presentation.booking.model

import android.os.Parcelable
import com.thekainchee.user.data.remote.dto.address.LocationDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingDetailUiModel(

    val bookingId: String,

    // Parlour
    val parlourName: String,
    val parlourPhone: String?,
    val parlourImages: List<String>?,
    val location: LocationDto?,

    // Staff
    val staffName: String,

    // Services
    val services: List<BookingServiceUiModel>,

    // Appointment
    val totalPrice: Double,
    val totalDurationMinutes: Int,
    val bookingDate: String,
    val slotStartTime: String,
    val slotEndTime: String,

    // Payment
    val paymentMethod: String?,
    val paymentStatus: String,

    // Booking
    val bookingStatus: String,
    val createdAt: String,

    // Cancellation
    val cancelledBy: String?,
    val cancelledAt: String?,
    val cancelReason: String?,

    // No Show
    val noShowAt: String?,
    val noShowReason: String?,
    val noShowPenalty: Double?,

    // Refund
    val walletRefundAmount: Double?
): Parcelable