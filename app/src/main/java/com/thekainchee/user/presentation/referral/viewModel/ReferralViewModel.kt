package com.thekainchee.user.presentation.referral.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ReferralRepository
import com.thekainchee.user.presentation.auth.state.ReferralState
import com.thekainchee.user.presentation.referral.state.ReferralHistoryState
import com.thekainchee.user.presentation.referral.state.WithdrawReferralState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val referralRepository: ReferralRepository
) : ViewModel() {
    private val _referralHistoryState =
        MutableStateFlow<ReferralHistoryState>(ReferralHistoryState.Idle)

    val referralHistoryState: StateFlow<ReferralHistoryState> =
        _referralHistoryState.asStateFlow()

    private val _withdrawReferralState =
        MutableStateFlow<WithdrawReferralState>(
            WithdrawReferralState.Idle
        )

    val withdrawReferralState: StateFlow<WithdrawReferralState> =
        _withdrawReferralState.asStateFlow()


    fun getReferralHistory() {
        viewModelScope.launch {

            _referralHistoryState.value = ReferralHistoryState.Loading

            referralRepository
                .getReferralHistory()
                .onSuccess { data ->
                    _referralHistoryState.value =
                        ReferralHistoryState.Success(data)
                }
                .onFailure { error ->
                    _referralHistoryState.value =
                        ReferralHistoryState.Error(
                            error.message
                                ?: "Failed to fetch referral history"
                        )
                }
        }
    }


    fun withdrawReferralReward() {
        viewModelScope.launch {

            _withdrawReferralState.value =
                WithdrawReferralState.Loading

            referralRepository
                .withdrawReferralReward()
                .onSuccess { amount ->
                    _withdrawReferralState.value =
                        WithdrawReferralState.Success(amount)

                    // Refresh referral data after withdrawal
                    getReferralHistory()
                }
                .onFailure { error ->
                    _withdrawReferralState.value =
                        WithdrawReferralState.Error(
                            error.message
                                ?: "Failed to withdraw referral reward"
                        )
                }
        }
    }
}