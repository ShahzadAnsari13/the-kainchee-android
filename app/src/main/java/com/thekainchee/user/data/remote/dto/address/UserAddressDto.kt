package com.thekainchee.user.data.remote.dto.address

data class UserAddressDto(
    val label: String,
    val location: LocationDto,
    val isDefault: Boolean
)
