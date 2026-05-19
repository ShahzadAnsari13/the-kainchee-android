package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.ParlourStatusResponseDto
import com.thekainchee.user.data.remote.dto.parlour.NearbyParlourResponseDto
import com.thekainchee.user.data.remote.dto.parlour.ParlourDetailsResponseDto
import com.thekainchee.user.data.remote.dto.service.ServiceCategoryDto
import com.thekainchee.user.data.remote.dto.parlour.TrendingParlourResponseDto
import com.thekainchee.user.data.remote.dto.parlour.TrendingServiceResponseDto
import com.thekainchee.user.data.remote.dto.parlour.UpcomingBookingResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ParlourApi {
    @GET("user/nearby-parlours")
    suspend fun getNearbyParlours(
        @Query("lat") lat : Double,
        @Query("lng") lng : Double,
        @Query("radius") radius : Int = 5,
        @Query("limit") limit : Int = 6,
        @Query("page") page :Int = 1,
        @Query("type") type :String?
    ): Response<NearbyParlourResponseDto>

    @GET("user/trending-parlours")
    suspend fun getTrendingParlours(
        @Query("lat") lat : Double,
        @Query("lng") lng : Double,
        @Query("radius") radius : Int = 5,
        @Query("type") type : String?
    ): Response<TrendingParlourResponseDto>

    @GET("user/trending-services")
    suspend fun getTrendingService(
        @Query("lat") lat : Double,
        @Query("lng") lng: Double
    ): Response<TrendingServiceResponseDto>

    @GET("user/upcoming-bookings")
    suspend fun getUpcomingBookings(
        @Query("limit") limit: Int
    ): Response<UpcomingBookingResponseDto>

    @GET("user/{id}/parlour-details")
    suspend fun getParlourDetails(
        @Path("id") id: String
    ): Response<ParlourDetailsResponseDto>

    @GET("parlour/{id}/service-categories")
    suspend fun getServiceCategories(
        @Path("id") id: String
    ): Response<ServiceCategoryDto>

    @GET("user/check-status/{parlourId}")
    suspend fun checkParlourStatus(
        @Path("parlourId") parlourId: String
    ): Response<ParlourStatusResponseDto>


}