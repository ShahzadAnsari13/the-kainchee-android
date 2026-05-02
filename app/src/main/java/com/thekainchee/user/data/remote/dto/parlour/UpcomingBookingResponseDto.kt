package com.thekainchee.user.data.remote.dto.parlour

import java.util.Date
import kotlin.time.Duration

data class UpcomingBookingResponseDto( val success: Boolean,
                                       val count: Int,
                                       val bookings: List<BookingDto>)