package com.alenear.user.data.remote.dto.parlour

data class TrendingServiceResponseDto(
    val success : Boolean,
    val count : Int,
    val data : List<ServiceDto>
)
