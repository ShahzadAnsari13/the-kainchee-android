package com.thekainchee.user.presentation.booking.state

import com.thekainchee.user.presentation.booking.model.BookingDetailUiModel

sealed class BookingDetailUiState {
    data object Idle : BookingDetailUiState()
    data object Loading : BookingDetailUiState()
    data class Success(val data: BookingDetailUiModel) : BookingDetailUiState()
    data class Error(val message: String) : BookingDetailUiState()

}