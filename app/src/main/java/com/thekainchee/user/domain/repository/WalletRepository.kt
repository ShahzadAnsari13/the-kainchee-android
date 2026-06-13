package com.thekainchee.user.domain.repository

interface WalletRepository {
    suspend fun getWalletBalance() : Result<Double>
}