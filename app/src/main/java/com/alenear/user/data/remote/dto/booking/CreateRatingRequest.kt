package com.alenear.user.data.remote.dto.booking

data class CreateRatingRequest(
    val parlourRating: Float,
    val staffRating: Float,
    val review: String
)