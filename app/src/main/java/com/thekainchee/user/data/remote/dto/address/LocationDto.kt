package com.thekainchee.user.data.remote.dto.address

data class LocationDto(
    val coordinates: List<Double>,
    val address: AddressDto,
    val manualAddress: ManualAddressDto
)
