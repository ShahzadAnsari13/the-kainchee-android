package com.thekainchee.user.presentation.service.model

data class ServiceUiModel(
    val id: String,
    val name: String,
    val price: Double,
    val duration: Int,
    val description: String?,
    val isAvailable: Boolean,
    val isAdded: Boolean = false
)