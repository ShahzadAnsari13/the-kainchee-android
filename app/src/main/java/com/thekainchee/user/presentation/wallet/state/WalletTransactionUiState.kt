package com.thekainchee.user.presentation.wallet.state

import com.thekainchee.user.presentation.wallet.model.WalletTransactionUiModel

sealed class WalletTransactionUiState {

    data object Idle : WalletTransactionUiState()

    data object Loading : WalletTransactionUiState()

    data class Success(
        val transactions: List<WalletTransactionUiModel>
    ) : WalletTransactionUiState()

    data object Empty : WalletTransactionUiState()

    data class Error(
        val message: String
    ) : WalletTransactionUiState()
}