package com.thekainchee.user.data.remote.dto.service

data class ServiceResponseDto(
    val success: Boolean,
    val count: Int,
    val services: List<ServiceItemDto>
)