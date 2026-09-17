package com.thekainchee.user.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    fun formatBookingDate(date: String): String {

        val today = LocalDate.now()

        val bookingDate = try {
            // ISO format
            Instant.parse(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } catch (e: Exception) {
            // LocalDate format
            LocalDate.parse(date)
        }

        return when (bookingDate) {
            today -> "Today"
            today.plusDays(1) -> "Tomorrow"
            else -> bookingDate.format(
                DateTimeFormatter.ofPattern("dd MMM")
            )
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

     fun formatDuration(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }
     fun formatBookingDay(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat(
                "EEEE",
                Locale.getDefault()
            )

            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}