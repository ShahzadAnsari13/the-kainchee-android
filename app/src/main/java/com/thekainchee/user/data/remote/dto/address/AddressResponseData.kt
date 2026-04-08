package com.thekainchee.user.data.remote.dto.address

data class AddressResponseData(
    val _id: String,
    val label: String,
    val location: LocationDto,
    val isDefault: Boolean
)
