package com.thekainchee.user.presentation.search.model

data class SearchParlourUiModel(
    val id: String,
    val name: String,
    val type: String,
    val rating: Double,
    val ratingCount: Int,
    val image: String?,
    val location: String,
    val latitude: Double,
    val longitude: Double
)