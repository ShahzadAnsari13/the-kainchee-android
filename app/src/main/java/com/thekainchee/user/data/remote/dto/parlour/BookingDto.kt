package com.thekainchee.user.data.remote.dto.parlour

import java.util.Date

data class BookingDto (
    val _id : String,
    val services : List<BookingServiceDto>,
    val totalPrice :Int,
    val totalDurationMinutes: Int,
    val bookingDate : String,
    val slotStartTime :String,
    val slotEndTime : String,
    val bookingStatus : String,
    val paymentStatus : String,
    val parlourId : String,
    val staffId : String,
    val createdAt : String)