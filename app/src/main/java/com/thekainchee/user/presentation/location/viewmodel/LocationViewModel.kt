package com.thekainchee.user.presentation.location.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.PlaceSearchRepository
import com.thekainchee.user.presentation.location.model.PlaceSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val placeSearchRepository: PlaceSearchRepository
) : ViewModel() {

    private var searchJob: Job? = null

    private val _searchResults = MutableLiveData<List<PlaceSuggestion>>()
    val searchResults: LiveData<List<PlaceSuggestion>> = _searchResults

    fun searchLocation(query: String) {

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(400)

            if (query.length >= 3) {
                val result = placeSearchRepository.searchLocation(query)
                _searchResults.postValue(result)
            } else {
                _searchResults.postValue(emptyList())
            }
        }
    }

}