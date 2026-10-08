package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.dashboard.model.BookingUI
import com.thekainchee.user.presentation.dashboard.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.model.ServiceUI
import com.thekainchee.user.presentation.parlour.model.ParlourDetailedUI

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
        type: String,
        lat : Double,
        lng : Double
    ): Result<List<ServiceUI>>

    suspend fun getUpcomingBookings(
        limit : Int
    ): Result<List<BookingUI>>

    suspend fun getParlourDetails(
        id : String
    ): Result<ParlourDetailedUI>


    suspend fun checkParlourStatus(
        parlourId: String
    ): Result<Boolean>


}