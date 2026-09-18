package com.thekainchee.user.presentation.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingServiceUiModel(
    val serviceId: String,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val image: String?
): Parcelable