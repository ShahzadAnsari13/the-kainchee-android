package com.thekainchee.user.presentation.dashboard.home.model

import com.thekainchee.user.data.remote.dto.parlour.BookingServiceDto

data class BookingUI(val id : String,
                     val serviceSummary : String,
                     val totalPrice :Int,
                     val totalDurationMinutes: Int,
                     val bookingDate : String,
                     val image: String,
                     val slotStartTime :String,
                     val slotEndTime : String,
                     val bookingStatus : String)
