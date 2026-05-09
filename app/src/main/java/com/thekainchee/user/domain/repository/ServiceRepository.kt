package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.service.model.ServiceCategory
import com.thekainchee.user.presentation.service.model.ServiceUiModel

interface ServiceRepository {
    suspend fun getServiceCategories(
        id : String
    ): Result<List<ServiceCategory>>

    suspend fun getServicesByCategory(
        parlourId: String,
        categoryId: String
    ): Result<List<ServiceUiModel>>

}