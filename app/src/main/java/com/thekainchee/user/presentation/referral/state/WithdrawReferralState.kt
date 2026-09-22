package com.thekainchee.user.presentation.referral.state

sealed class WithdrawReferralState {

    data object Idle : WithdrawReferralState()

    data object Loading : WithdrawReferralState()

    data class Success(
        val amount: Int
    ) : WithdrawReferralState()

    data class Error(
        val message: String
    ) : WithdrawReferralState()
}