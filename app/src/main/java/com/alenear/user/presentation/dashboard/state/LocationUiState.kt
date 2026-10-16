package com.alenear.user.presentation.dashboard.state

import com.alenear.user.domain.model.UserAddress

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