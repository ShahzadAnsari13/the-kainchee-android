package com.thekainchee.user.presentation.search.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.SearchRepository
import com.thekainchee.user.presentation.search.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _searchState =
        MutableStateFlow<SearchState>(SearchState.Idle)

    val searchState: StateFlow<SearchState> = _searchState

    private var searchJob: Job? = null

    private var currentQuery = ""
    private var currentType: String? = null
    private var currentMinRating: Double? = null
    private var currentPage = 1

    private var isLoadingMore = false
    private var hasMore = true
    private var latitude: Double? = null
    private var longitude: Double? = null

    fun searchParlours(
        query: String,
        type: String? = null,
        minRating: Double? = null
    ) {
        val lat = latitude ?: return
        val lng = longitude ?: return

        currentQuery = query
        currentType = type
        currentMinRating = minRating
        currentPage = 1
        hasMore = true

        isLoadingMore = false

        searchJob?.cancel()

        _searchState.value = SearchState.Loading

        searchJob = viewModelScope.launch {

            val result = repository.searchParlours(
                latitude = lat,
                longitude = lng,
                query = currentQuery,
                type = currentType,
                minRating = currentMinRating,
                page = currentPage
            )

            result.onSuccess { data ->

                if (data.isEmpty()) {
                    _searchState.value = SearchState.Empty
                } else {
                    _searchState.value =
                        SearchState.Success(data)
                }

            }.onFailure { error ->

                if (error is CancellationException) {
                    return@onFailure
                }

                _searchState.value = SearchState.Error(
                    error.message ?: "Failed to search parlours"
                )
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchState.value = SearchState.Idle
    }
    fun loadNextPage() {

        val lat = latitude ?: return
        val lng = longitude ?: return
        if (isLoadingMore || !hasMore) return

        val currentState = _searchState.value

        if (currentState !is SearchState.Success) return


        isLoadingMore = true

        val nextPage = currentPage + 1

        viewModelScope.launch {

            val result = repository.searchParlours(
                latitude = lat,
                longitude = lng,
                query = currentQuery,
                type = currentType,
                minRating = currentMinRating,
                page = nextPage
            )

            result.onSuccess { data ->

                if (data.isEmpty()) {
                    hasMore = false
                } else {

                    currentPage = nextPage

                    _searchState.value = SearchState.Success(
                        currentState.parlours + data
                    )
                }

                isLoadingMore = false

            }.onFailure { error ->

                if (error is CancellationException) {
                    isLoadingMore = false
                    return@onFailure
                }

                isLoadingMore = false
            }
        }
    }

    fun setLatLng(lat: Double, lng: Double):Unit {
        latitude = lat
        longitude = lng
    }
    fun getCurrentType(): String? = currentType

    fun getCurrentMinRating(): Double? = currentMinRating

}