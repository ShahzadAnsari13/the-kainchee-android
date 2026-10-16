package com.alenear.user.data.remote.dto.service

data class BookingPreviewRequest(
    val parlourId : String,
    val serviceIds : List<String>
)