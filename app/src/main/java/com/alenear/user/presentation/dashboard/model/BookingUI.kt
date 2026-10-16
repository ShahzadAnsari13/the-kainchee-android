package com.alenear.user.presentation.dashboard.model

data class BookingUI(val id : String,
                     val serviceSummary : String,
                     val totalPrice :Int,
                     val totalDurationMinutes: Int,
                     val bookingDate : String,
                     val image: String,
                     val slotStartTime :String,
                     val slotEndTime : String,
                     val bookingStatus : String)
