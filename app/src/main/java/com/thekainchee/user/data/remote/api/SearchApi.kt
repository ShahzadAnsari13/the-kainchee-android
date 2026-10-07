package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.search.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("user/search/parlours")
    suspend fun searchParlours(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("q") query: String,
        @Query("type") type: String? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1
    ): Response<SearchResponse>
}