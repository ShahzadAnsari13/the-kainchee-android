package com.thekainchee.user.presentation.dashboard.home.state

import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI

sealed class TrendingServiceState {

    object Idle : TrendingServiceState()

    object Loading : TrendingServiceState()

    data class Success( val data : List<ServiceUI>) : TrendingServiceState()

    data class Error(val message : String) : TrendingServiceState()
}