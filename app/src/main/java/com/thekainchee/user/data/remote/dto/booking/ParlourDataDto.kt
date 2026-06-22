package com.thekainchee.user.data.remote.dto.booking

import com.thekainchee.user.data.remote.dto.address.LocationDto

data class ParlourDataDto(
    val _id: String,
    val name: String,
    val contactNumber: String?,
    val location: LocationDto?
)