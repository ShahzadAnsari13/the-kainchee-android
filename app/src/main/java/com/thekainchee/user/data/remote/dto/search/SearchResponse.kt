package com.thekainchee.user.data.remote.dto.search

import com.thekainchee.user.data.remote.dto.parlour.RatingDto

data class SearchResponse(
    val success: Boolean,
    val page: Int,
    val count: Int,
    val parlours: List<SearchParlourDto>
)

data class SearchParlourDto(
    val _id: String,
    val name: String,
    val type: String,
    val rating: RatingDto,
    val images: List<String>,
    val location: SearchLocationDto,
    val distance : Double
)
data class SearchLocationDto(
    val type: String,
    val coordinates: List<Double>,
    val address: SearchAddressDto,
    val manualAddress: SearchManualAddressDto?
)

data class SearchAddressDto(
    val country: String,
    val state: String,
    val district: String,
    val city: String?,
    val pincode: String?
)
data class SearchManualAddressDto(
    val landmark: String?,
    val details: String?
)
