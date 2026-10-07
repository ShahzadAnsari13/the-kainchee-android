package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.search.model.SearchParlourUiModel

interface SearchRepository {
    suspend fun searchParlours(
        query: String,
        type: String? = null,
        minRating: Double? = null,
        limit: Int = 5,
        page: Int = 1
    ): Result<List<SearchParlourUiModel>>
}