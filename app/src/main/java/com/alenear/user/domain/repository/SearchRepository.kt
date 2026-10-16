package com.alenear.user.domain.repository

import com.alenear.user.presentation.search.model.SearchParlourUiModel

interface SearchRepository {
    suspend fun searchParlours(
        latitude: Double,
        longitude: Double,
        query: String,
        type: String? = null,
        minRating: Double? = null,
        limit: Int = 5,
        page: Int = 1
    ): Result<List<SearchParlourUiModel>>
}