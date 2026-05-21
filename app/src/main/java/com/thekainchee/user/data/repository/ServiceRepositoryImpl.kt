package com.thekainchee.user.data.repository

import android.util.Log
import com.thekainchee.user.data.local.room.dao.SelectedServiceDao
import com.thekainchee.user.data.local.room.entity.SelectedServiceEntity
import com.thekainchee.user.data.mapper.toUI
import com.thekainchee.user.data.remote.api.ServiceApi
import com.thekainchee.user.data.remote.dto.service.BookingPreviewRequest
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.presentation.service.model.BookingPreviewUiModel
import com.thekainchee.user.presentation.service.model.ServiceCategory
import com.thekainchee.user.presentation.service.model.ServiceUiModel
import com.thekainchee.user.utils.ErrorUtils
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(private val api  : ServiceApi, private val dao : SelectedServiceDao) :
    ServiceRepository {
        override suspend fun getServiceCategories(id: String): Result<List<ServiceCategory>> {
    return  try{
        val response = api.getServiceCategories(id)
        if(response.isSuccessful && response.body() != null){
            val body = response.body()
            val list = body?.data?.map { it.toUI() }.orEmpty()
            Result.success(list)
        }else{
            val error = ErrorUtils.parseError(response.errorBody()?.string())
            Result.failure(Exception(error.message ?: "Failed to fetch service categories"))
        }

    }catch(e: Exception){
        Result.failure(e)
    }
}

    override suspend fun getServicesByCategory(
        parlourId: String,
        categoryId: String
    ): Result<List<ServiceUiModel>> {
        try{
            val response = api.getServicesByCategory(parlourId,categoryId)
            if(response.isSuccessful && response.body() !=null){
                val body = response.body()
                val list = body?.services?.map { it.toUI() }.orEmpty()
                return Result.success(list)
            }else{
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                return Result.failure(Exception(error.message ?: "Failed to fetch services"))
            }
        }
        catch(e: Exception){
            return Result.failure(e)

        }
    }

    override suspend fun insert(parlourId: String,serviceId: String){
        dao.insertSelectedService(SelectedServiceEntity(
            parlourId = parlourId,
            serviceId = serviceId))
    }

    override suspend fun remove(parlourId: String,serviceId: String){
        dao.removeService(
            parlourId = parlourId,
            serviceId = serviceId)
    }

    override suspend fun getAll( parlourId: String):
            List<SelectedServiceEntity> {

        return dao.getSelectedServices(parlourId)
    }

    override suspend fun clearAll(parlourId: String) {
        dao.clearAllService(parlourId)
    }

    override suspend fun getBookingPreview(
        parlourId: String,
        serviceIds: List<String>
    ): Result<BookingPreviewUiModel> {

        Log.d("PARLOURID",parlourId)
        try {

            val response =
                api.getBookingPreview(
                    BookingPreviewRequest(
                        parlourId,
                        serviceIds
                    )
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body = response.body()!!

                val data = body.toUI()

                return Result.success(data)

            } else {

                val error =
                    ErrorUtils.parseError(
                        response.errorBody()?.string()
                    )

                return Result.failure(
                    Exception(
                        error.message
                            ?: "Failed to fetch services"
                    )
                )
            }

        } catch (e: Exception) {

            return Result.failure(e)
        }
    }
}