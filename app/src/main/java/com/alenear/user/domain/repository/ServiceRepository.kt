package com.alenear.user.domain.repository

import com.alenear.user.data.local.room.entity.SelectedServiceEntity
import com.alenear.user.presentation.service.model.BookingPreviewUiModel
import com.alenear.user.presentation.service.model.ServiceCategory
import com.alenear.user.presentation.service.model.ServiceUiModel

interface ServiceRepository {
    suspend fun getServiceCategories(
        id : String
    ): Result<List<ServiceCategory>>

    suspend fun getServicesByCategory(
        parlourId: String,
        categoryId: String
    ): Result<List<ServiceUiModel>>

    suspend fun insert(parlourId: String,serviceId : String)
    suspend fun remove(parlourId: String,serviceId: String)
    suspend fun getAll(parlourId: String) : List<SelectedServiceEntity>
    suspend fun clearAll(parlourId: String)

    suspend fun getBookingPreview(parlourId: String,serviceIds : List<String>) : Result<BookingPreviewUiModel>

}