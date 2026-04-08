package com.thekainchee.user.presentation.location.model

data class AddressUI(
    val id: String?,
    val label: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String? = null,
    val isSelected: Boolean = false,
    val isFromSearch: Boolean = false
)