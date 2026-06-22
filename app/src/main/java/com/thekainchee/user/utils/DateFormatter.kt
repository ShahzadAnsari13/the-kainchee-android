package com.thekainchee.user.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    fun formatBookingDate(date: String): String {
        val today = java.time.LocalDate.now()
        val booking = java.time.LocalDate.parse(date)

        return when (booking) {
            today -> "Today"
            today.plusDays(1) -> "Tomorrow"
            else -> booking.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM"))
        }
    }

    fun formatBookingSuccessDate(date: String): String {

        val today = LocalDate.now()

        val bookingDate = Instant.parse(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return when (bookingDate) {
            today -> "Today"
            today.plusDays(1) -> "Tomorrow"
            else -> bookingDate.format(
                DateTimeFormatter.ofPattern("dd MMM")
            )
        }
    }
}