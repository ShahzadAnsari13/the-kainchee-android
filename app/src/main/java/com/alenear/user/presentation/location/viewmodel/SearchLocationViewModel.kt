package com.alenear.user.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alenear.user.domain.repository.PlaceSearchRepository
import com.alenear.user.presentation.location.model.PlaceSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val placeSearchRepository: PlaceSearchRepository
) : ViewModel() {

    private var searchJob: Job? = null

    private val _searchResults = MutableStateFlow<List<PlaceSuggestion>>(emptyList())
    val searchResults: StateFlow<List<PlaceSuggestion>> = _searchResults

    fun searchLocation(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(200)

            if (query.length >= 3) {
                try {
                    val result = placeSearchRepository.searchLocation(query)
                    _searchResults.value = result
                } catch (e: Exception) {
                    _searchResults.value = emptyList()
                }
            } else {
                _searchResults.value = emptyList()
            }
        }
    }

}