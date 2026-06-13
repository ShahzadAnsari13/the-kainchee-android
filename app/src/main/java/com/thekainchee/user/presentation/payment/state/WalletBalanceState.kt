package com.thekainchee.user.presentation.payment.state

sealed class WalletBalanceState {
    object Idle : WalletBalanceState()
    object Loading : WalletBalanceState()
    data class Success(val balance: Double) : WalletBalanceState()
    data class Error(val message: String) : WalletBalanceState()

}