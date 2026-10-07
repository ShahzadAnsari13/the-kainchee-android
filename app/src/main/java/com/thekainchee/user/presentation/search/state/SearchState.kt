package com.thekainchee.user.presentation.search.state

import com.thekainchee.user.presentation.search.model.SearchParlourUiModel

sealed class SearchState {

    data object Idle : SearchState()

    data object Loading : SearchState()

    data class Success(
        val parlours: List<SearchParlourUiModel>
    ) : SearchState()

    data object Empty : SearchState()

    data class Error(
        val message: String
    ) : SearchState()
}