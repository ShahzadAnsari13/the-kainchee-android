package com.thekainchee.user.presentation.payment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.WalletRepository
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.payment.state.WalletBalanceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val repository: WalletRepository) : ViewModel(){
    private val _walletBalanceState = MutableStateFlow<WalletBalanceState>(WalletBalanceState.Idle)
    val walletBalanceState : StateFlow<WalletBalanceState> = _walletBalanceState

    fun getWalletBalance(){
        _walletBalanceState.value = WalletBalanceState.Loading
        viewModelScope.launch {
            val result = repository.getWalletBalance()
            result.onSuccess { balance ->
                _walletBalanceState.value = WalletBalanceState.Success(balance)
            }.onFailure { error ->
                _walletBalanceState.value =
                    WalletBalanceState.Error(error.message ?: "Failed to fetch wallet balance")
            }
        }
    }
}