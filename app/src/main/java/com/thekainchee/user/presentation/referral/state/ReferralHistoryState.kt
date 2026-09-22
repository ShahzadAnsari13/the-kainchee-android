package com.thekainchee.user.presentation.referral.state

import com.thekainchee.user.presentation.referral.model.ReferralHistory

sealed class ReferralHistoryState {
    data object Idle : ReferralHistoryState()

    data object Loading : ReferralHistoryState()

    data class Success(
        val data: ReferralHistory
    ) : ReferralHistoryState()

    data class Error(
        val message: String
    ) : ReferralHistoryState()
}