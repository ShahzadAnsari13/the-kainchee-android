package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.service.model.ServiceCategory

interface ServiceRepository {
    suspend fun getServiceCategories(
        id : String
    ): Result<List<ServiceCategory>>
}