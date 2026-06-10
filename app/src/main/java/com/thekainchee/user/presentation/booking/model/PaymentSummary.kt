package com.thekainchee.user.presentation.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentSummary(
    val bookingId: String,
    val staffName: String,
    val dateTime: String,
    val amount: String
): Parcelable
