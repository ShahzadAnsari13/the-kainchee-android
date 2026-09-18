package com.thekainchee.user.data.remote.dto.address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationDto(
    val coordinates: List<Double>,
    val address: AddressDto,
    val manualAddress: ManualAddressDto
): Parcelable
