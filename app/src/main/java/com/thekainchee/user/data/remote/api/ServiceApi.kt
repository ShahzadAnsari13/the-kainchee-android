package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.parlour.ServiceCategoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ServiceApi {
    @GET("parlour/{id}/service-categories")
    suspend fun getServiceCategories(
        @Path("id") id: String
    ): Response<ServiceCategoryDto>

    @GET("user/{parlourId}/{categoryId}/services")
    suspend fun getServicesByCategory(
        @Path("parlourId") parlourId: String,
        @Path("categoryId") categoryId: String
    ): Response<>
}