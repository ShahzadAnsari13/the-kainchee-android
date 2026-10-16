package com.alenear.user.domain.repository

import com.alenear.user.presentation.location.model.PlaceSuggestion

interface PlaceSearchRepository {
    suspend fun searchLocation(query: String): List<PlaceSuggestion>
}