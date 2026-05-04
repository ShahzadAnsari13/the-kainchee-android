package com.thekainchee.user.domain.repository

import com.thekainchee.user.data.remote.dto.parlour.BookingDto
import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI
import com.thekainchee.user.presentation.parlour.model.ParlourDetailedUI
import com.thekainchee.user.presentation.service.model.ServiceCategory

interface ParlourRepository {
    suspend fun getNearbyParlours(
        lat : Double,
        lng : Double,
        page : Int,
        type : String?
    ): Result<List<ParlourUI>>


    suspend fun getTrendingParlours(
        lat : Double,
        lng : Double,
        type : String?
    ): Result<List<ParlourUI>>

    suspend fun  getTrendingServices(
        lat : Double,
        lng : Double
    ): Result<List<ServiceUI>>

    suspend fun getUpcomingBookings(
        limit : Int
    ): Result<List<BookingUI>>

    suspend fun getParlourDetails(
        id : String
    ): Result<ParlourDetailedUI>



}