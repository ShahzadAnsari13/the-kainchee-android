package com.thekainchee.user.data.repository

import com.thekainchee.user.data.mapper.toUI
import com.thekainchee.user.data.mapper.toUi
import com.thekainchee.user.data.remote.api.ParlourApi
import com.thekainchee.user.domain.repository.ParlourRepository
import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI
import com.thekainchee.user.presentation.parlour.model.ParlourDetailedUI
import com.thekainchee.user.presentation.service.model.ServiceCategory
import com.thekainchee.user.utils.ErrorUtils
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.orEmpty
import kotlin.math.ln

class ParlourRepositoryImpl @Inject constructor( private val api: ParlourApi) : ParlourRepository {
    override suspend fun getNearbyParlours(
        lat: Double,
        lng: Double,
        page: Int,
        type: String?
    ): Result<List<ParlourUI>> {
        try {
            val response = api.getNearbyParlours(lat = lat, lng = lng, page = page, type = type)
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()?.parlours?.map { it.toUI() }.orEmpty()
                return Result.success(list)
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                return Result.failure(Exception(error.message ?: "Failed to fetch parlours"))
            }
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }




    override suspend fun getTrendingParlours(
        lat: Double,
        lng: Double,
        type: String?
    ): Result<List<ParlourUI>> {
        try {
            val response = api.getTrendingParlours(lat = lat, lng = lng, type = type)
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()?.data?.map { it.toUI() }.orEmpty()
                return Result.success(list)
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                return Result.failure(Exception(error.message ?: "Failed to fetch parlours"))

            }
        }
        catch (e: Exception) {
            return Result.failure(e)
        }

    }

    override suspend fun getTrendingServices(
        lat: Double,
        lng: Double
    ): Result<List<ServiceUI>> {
        try{
            val response = api.getTrendingService(lat,lng)
            if(response.isSuccessful && response.body() != null){
                val list =  response.body()?.data?.map { it.toUI() }.orEmpty()
                return Result.success(list)
            }else{
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                return Result.failure(Exception(error.message ?: "Failed to fetch services"))
            }
        }catch(e: Exception){
           return Result.failure(e)
        }
    }

    override suspend fun getUpcomingBookings(limit: Int): Result<List<BookingUI>> {
        try{
            val response = api.getUpcomingBookings(limit)
            if(response.isSuccessful && response.body()!=null){
                val list = response.body()?.bookings?.map { it.toUI() }.orEmpty()
                return Result.success(list)
            }else{
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                return Result.failure(Exception(error.message ?: "Failed to fetch bookings"))
            }
        }catch (e: IOException) {
            return Result.failure(Exception("No internet connection"))
        }
        catch(e: Exception){
            return Result.failure(e)
        }
    }


    override suspend fun getParlourDetails(id: String): Result<ParlourDetailedUI> {
        return try {
            val response = api.getParlourDetails(id)

            if (response.isSuccessful && response.body() != null) {

                val dto = response.body()!!

                val ui = dto.toUi()   // 🔥 direct mapper call

                Result.success(ui)

            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to fetch parlour details"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkParlourStatus(parlourId: String): Result<Boolean> {
        return try{
            val response = api.checkParlourStatus(parlourId)
            val dto = response.body()
            if(response.isSuccessful && dto != null){

                Result.success(dto.isOpenNow)
            }else{
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to check parlour status"))
            }
        }
        catch (e : Exception){
            Result.failure(e)
        }
    }


}