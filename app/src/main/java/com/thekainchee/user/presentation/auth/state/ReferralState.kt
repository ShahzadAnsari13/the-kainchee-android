package com.thekainchee.user.presentation.auth.state

sealed class ReferralState {

    data object Idle : ReferralState()

    data object Loading : ReferralState()

    data class Success(
        val message: String
    ) : ReferralState()

    data class Error(
        val message: String
    ) : ReferralState()
}