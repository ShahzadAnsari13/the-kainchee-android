package com.thekainchee.user.data.remote.dto.parlour

data class ParlourDto(
    val _id: String,
    val name: String,
    val images: List<String>?,
    val rating: RatingDto,
    val distance: Double,
    val type: String
)