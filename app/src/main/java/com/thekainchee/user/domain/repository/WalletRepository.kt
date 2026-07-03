package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.wallet.model.WalletTransactionUiModel

interface WalletRepository {
    suspend fun getWalletBalance() : Result<Double>
    suspend fun getWalletTransactions() : Result<List<WalletTransactionUiModel>>
}