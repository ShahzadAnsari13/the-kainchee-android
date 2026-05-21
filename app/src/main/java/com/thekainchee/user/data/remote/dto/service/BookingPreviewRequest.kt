package com.thekainchee.user.data.remote.dto.service

data class BookingPreviewRequest(
    val parlourId : String,
    val serviceIds : List<String>
)