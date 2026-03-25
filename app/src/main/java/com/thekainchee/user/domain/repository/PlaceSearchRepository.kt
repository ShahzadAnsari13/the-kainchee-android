package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.location.model.PlaceSuggestion

interface PlaceSearchRepository {
    suspend fun searchLocation(query: String): List<PlaceSuggestion>
}