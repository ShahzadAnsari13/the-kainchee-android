package com.thekainchee.user.presentation.service.model

data class ServiceUiModel(
    val id: String,
    val name: String,
    val price: Double,
    val image: String,
    val duration: Int,
    val description: String?,
    val isAvailable: Boolean,
    var isAdded: Boolean = false
)