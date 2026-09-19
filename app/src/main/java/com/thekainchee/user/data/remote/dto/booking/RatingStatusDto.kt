package com.thekainchee.user.data.remote.dto.booking

data class RatingStatusDto(
    val success: Boolean,
    val status: String,
    val rating: RatingDataDto?
)
data class RatingDataDto(
    val parlourRating: Float,
    val staffRating: Float,
    val review: String?,
    val createdAt: String?
)