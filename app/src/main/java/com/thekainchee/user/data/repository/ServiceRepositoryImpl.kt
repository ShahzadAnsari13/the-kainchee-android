package com.thekainchee.user.data.repository

import com.thekainchee.user.data.mapper.toUI
import com.thekainchee.user.data.remote.api.ServiceApi
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.presentation.service.model.ServiceCategory
import com.thekainchee.user.utils.ErrorUtils
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(private val api  : ServiceApi) :
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
}