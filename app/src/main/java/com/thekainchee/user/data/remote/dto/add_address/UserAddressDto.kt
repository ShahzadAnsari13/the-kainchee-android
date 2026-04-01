package com.thekainchee.user.data.remote.dto.add_address

data class UserAddressDto(
    val label: String,
    val location: LocationDto,
    val isDefault: Boolean
)
