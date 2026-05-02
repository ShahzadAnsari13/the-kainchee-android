package com.thekainchee.user.data.remote.dto.parlour

data class NearbyParlourResponseDto(
    val success: Boolean,
    val page: Int,
    val limit: Int,
    val count: Int,
    val parlours: List<ParlourDto>
)