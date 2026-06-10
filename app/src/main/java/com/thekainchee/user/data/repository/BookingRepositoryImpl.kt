package com.thekainchee.user.data.repository

import com.thekainchee.user.data.mapper.toBookingRequestDto
import com.thekainchee.user.data.mapper.toBookingUiModel
import com.thekainchee.user.data.mapper.toUi
import com.thekainchee.user.data.remote.api.BookingApi
import com.thekainchee.user.domain.repository.BookingRepository
import com.thekainchee.user.presentation.booking.model.BookingUiModel
import com.thekainchee.user.presentation.booking.model.CreateBookingParams
import com.thekainchee.user.presentation.booking.model.SlotUiModel
import com.thekainchee.user.presentation.booking.model.StaffUiModel
import com.thekainchee.user.utils.ErrorUtils
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(private val api: BookingApi) : BookingRepository {


    override
    suspend fun getParlourStaffs(parlourId: String): Result<List<StaffUiModel>> {

        return try {
            val response = api.getParlourStaffs(parlourId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                val list = body?.staff?.map { it.toUi() }.orEmpty()
                Result.success(list)
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to fetch staff"))
            }
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override
    suspend fun getStaffSlots(
        parlourId: String,
        staffId: String,
        date: String
    ): Result<List<SlotUiModel>> {
        return try {
            val response = api.getStaffSlots(parlourId, staffId, date)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val list = body.slots.map { SlotUiModel(time = it.start) }
                Result.success(list)
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to fetch staff"))
            }
        }
        catch (e: Exception) {
            Result.failure(e)
            }
    }

    override suspend fun createBooking(params: CreateBookingParams): Result<BookingUiModel> {
        return try {
            val response = api.createBooking(params.toBookingRequestDto())
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.toBookingUiModel())
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to fetch staff"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }
}