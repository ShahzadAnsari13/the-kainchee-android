package com.thekainchee.user.presentation.dashboard.home.state

import com.thekainchee.user.domain.model.UserAddress

sealed class LocationUiState {

    data object Idle : LocationUiState()

    data object Loading : LocationUiState()

    data class Success(
        val address: UserAddress
    ) : LocationUiState()

    data class Error(
        val message: String
    ) : LocationUiState()
}