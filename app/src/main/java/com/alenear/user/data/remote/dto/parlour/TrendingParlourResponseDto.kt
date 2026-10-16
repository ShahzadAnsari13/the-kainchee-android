package com.alenear.user.data.remote.dto.parlour

data class TrendingParlourResponseDto(
    val success: Boolean,
    val count: Int,
    val data: List<ParlourDto>
)