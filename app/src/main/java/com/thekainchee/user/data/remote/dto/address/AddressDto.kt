package com.thekainchee.user.data.remote.dto.address

data class AddressDto(
    val country: String,
    val state: String,
    val district: String,
    val city: String?,
    val pincode: String?
)
