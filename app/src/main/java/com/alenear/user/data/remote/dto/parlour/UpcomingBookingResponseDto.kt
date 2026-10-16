package com.alenear.user.data.remote.dto.parlour

data class UpcomingBookingResponseDto( val success: Boolean,
                                       val count: Int,
                                       val bookings: List<BookingDto>)