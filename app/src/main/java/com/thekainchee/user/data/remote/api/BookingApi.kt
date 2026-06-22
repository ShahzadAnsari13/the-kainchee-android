package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.booking.BookingDetailResponseDto
import com.thekainchee.user.data.remote.dto.booking.BookingRequestDto
import com.thekainchee.user.data.remote.dto.booking.BookingResponseDto
import com.thekainchee.user.data.remote.dto.booking.SlotResponseDto
import com.thekainchee.user.data.remote.dto.booking.StaffDto
import com.thekainchee.user.data.remote.dto.booking.StaffResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApi {
    @GET("user/parlour-staffs/{parlourId}")
    suspend fun getParlourStaffs(
        @Path("parlourId") parlourId: String
    ): Response<StaffResponseDto>

    @GET("user/parlours/{parlourId}/staffs/{staffId}/slots")
    suspend fun getStaffSlots(
        @Path("parlourId") parlourId: String,
        @Path("staffId") staffId: String,
        @Query("date") date: String
    ): Response<SlotResponseDto>

    @POST("user/create-booking")
    suspend fun createBooking(
        @Body bookingRequest: BookingRequestDto
    ): Response<BookingResponseDto>

    @GET ("user/booking-detail/{bookingId}")
    suspend fun getBookingDetails(
        @Path("bookingId") bookingId: String
    ): Response<BookingDetailResponseDto>


}
