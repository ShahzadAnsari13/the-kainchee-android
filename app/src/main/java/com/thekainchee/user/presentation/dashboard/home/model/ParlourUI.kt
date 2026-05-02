package com.thekainchee.user.presentation.dashboard.home.model

data class ParlourUI(
    val id: String,
    val name: String,
    val image: String?,
    val rating: Double,
    val distance: Double,
    val type: String,
    val isTrending: Boolean = false
)
