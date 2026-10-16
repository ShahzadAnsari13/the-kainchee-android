package com.alenear.user.data.repository

import com.alenear.user.data.mapper.toUI
import com.alenear.user.data.remote.api.SearchApi
import com.alenear.user.domain.repository.SearchRepository
import com.alenear.user.presentation.search.model.SearchParlourUiModel
import com.alenear.user.utils.ErrorUtils
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: SearchApi
) : SearchRepository {

    override suspend fun searchParlours(
        latitude: Double,
        longitude: Double,
        query: String,
        type: String?,
        minRating: Double?,
        limit: Int,
        page: Int
    ): Result<List<SearchParlourUiModel>> {

        return try {

            val response = api.searchParlours(
                latitude = latitude,
                longitude = longitude,
                query = query,
                type = type,
                minRating = minRating,
                limit = limit,
                page = page
            )

            if (response.isSuccessful && response.body() != null) {

                val data = response.body()!!.parlours

                if (data.isEmpty()) {
                    Result.success(emptyList())
                } else {
                    Result.success(
                        data.map { it.toUI() }
                    )
                }

            } else {

                val errorBody = response.errorBody()?.string()
                val error = ErrorUtils.parseError(errorBody)

                Result.failure(
                    Exception(
                        error.message ?: "Failed to search parlours"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}