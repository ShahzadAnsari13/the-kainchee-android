package com.thekainchee.user.data.remote.dto.address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressDto(
    val country: String,
    val state: String,
    val district: String,
    val city: String?,
    val pincode: String?
): Parcelable
