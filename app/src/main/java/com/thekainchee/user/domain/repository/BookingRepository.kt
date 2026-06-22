package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.booking.model.BookingDetailUiModel
import com.thekainchee.user.presentation.booking.model.BookingUiModel
import com.thekainchee.user.presentation.booking.model.CreateBookingParams
import com.thekainchee.user.presentation.booking.model.SlotUiModel
import com.thekainchee.user.presentation.booking.model.StaffUiModel

interface BookingRepository {
    suspend fun getParlourStaffs(parlourId: String): Result<List<StaffUiModel>>

    suspend fun getStaffSlots(parlourId: String, staffId: String, date: String): Result<List<SlotUiModel>>

    suspend fun createBooking(params: CreateBookingParams): Result<BookingUiModel>

    suspend fun getBookingDetails(bookingId: String): Result<BookingDetailUiModel>
}