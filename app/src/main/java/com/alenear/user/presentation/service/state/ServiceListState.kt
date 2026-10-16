package com.alenear.user.presentation.service.state

import com.alenear.user.presentation.service.model.ServiceUiModel

sealed class ServiceListState {

    object Idle : ServiceListState()
    object Loading : ServiceListState()
    data class Success(val data: List<ServiceUiModel>) : ServiceListState()
    object Empty : ServiceListState()
    data class Error(val message: String) : ServiceListState()
}