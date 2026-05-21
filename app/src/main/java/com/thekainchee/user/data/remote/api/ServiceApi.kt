package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.service.BookingPreviewRequest
import com.thekainchee.user.data.remote.dto.service.BookingPreviewResponseDto
import com.thekainchee.user.data.remote.dto.service.ServiceCategoryDto
import com.thekainchee.user.data.remote.dto.service.ServiceResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApi {
    @GET("parlour/{id}/service-categories")
    suspend fun getServiceCategories(
        @Path("id") id: String
    ): Response<ServiceCategoryDto>

    @GET("user/{parlourId}/services")
    suspend fun getServicesByCategory(
        @Path("parlourId") parlourId: String,
        @Query("categoryId") categoryId: String
    ): Response<ServiceResponseDto>

    @POST("user/booking-preview")
    suspend fun getBookingPreview(
        @Body bookingPreviewRequest: BookingPreviewRequest
    ): Response<BookingPreviewResponseDto>
}