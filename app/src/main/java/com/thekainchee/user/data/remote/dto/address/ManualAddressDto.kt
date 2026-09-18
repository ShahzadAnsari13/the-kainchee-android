package com.thekainchee.user.data.remote.dto.address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ManualAddressDto(
    val landmark: String?,
    val details: String?
): Parcelable
