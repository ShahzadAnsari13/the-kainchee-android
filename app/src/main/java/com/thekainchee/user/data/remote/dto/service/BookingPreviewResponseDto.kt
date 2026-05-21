package com.thekainchee.user.data.remote.dto.service

data class BookingPreviewResponseDto(
    val success : Boolean,
    val parlourId: String,
    val totalPrice : Int,
    val services : List<BookingPreviewDto>,
    val totalDuration : Int,
    val totalServices : Int
)