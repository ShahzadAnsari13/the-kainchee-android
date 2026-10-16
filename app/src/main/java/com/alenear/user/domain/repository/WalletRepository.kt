package com.alenear.user.domain.repository

import com.alenear.user.presentation.wallet.model.WalletTransactionUiModel

interface WalletRepository {
    suspend fun getWalletBalance() : Result<Double>
    suspend fun getWalletTransactions() : Result<List<WalletTransactionUiModel>>
}