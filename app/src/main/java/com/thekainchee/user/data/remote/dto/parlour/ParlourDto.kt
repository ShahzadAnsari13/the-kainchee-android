package com.thekainchee.user.data.remote.dto.parlour

import com.thekainchee.user.data.remote.dto.search.SearchLocationDto

data class ParlourDto(
    val _id: String,
    val name: String,
    val images: List<String>?,
    val rating: RatingDto,
    val distance: Double,
    val type: String,
    val location: SearchLocationDto
)