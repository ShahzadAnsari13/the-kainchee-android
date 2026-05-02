package com.thekainchee.user.presentation.parlour.model

data class LocationUiModel(
    val latitude: Double,
    val longitude: Double,

    val country: String,
    val state: String,
    val district: String,
    val city: String?,
    val pincode: String?,

    val landmark: String?,
    val details: String?
)