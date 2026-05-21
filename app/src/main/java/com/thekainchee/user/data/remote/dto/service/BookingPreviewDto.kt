package com.thekainchee.user.data.remote.dto.service

data class BookingPreviewDto(
    val id : String,
    val name :String,
    val price : Int,
    val durationMinutes : Int,
    val image : String
)