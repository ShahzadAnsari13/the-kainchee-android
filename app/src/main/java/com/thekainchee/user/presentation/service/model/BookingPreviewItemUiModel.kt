package com.thekainchee.user.presentation.service.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingPreviewItemUiModel(

    val id : String,
    val name: String,

    val price: Int,

    val duration: Int,

    val image: String
): Parcelable
