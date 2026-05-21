package com.thekainchee.user.presentation.service.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingPreviewUiModel(
    val parlourId : String,
    val services : List<BookingPreviewItemUiModel>,
    val totalPrice : Int,
    val totalDuration : Int,
    val totalServices : Int
): Parcelable