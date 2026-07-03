package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.wallet.WalletBalanceResponseDto
import com.thekainchee.user.data.remote.dto.wallet.WalletTransactionResponse
import retrofit2.Response

import retrofit2.http.GET

interface WalletApi {

    @GET("wallet/balance")
    suspend fun getWalletBalance(): Response<WalletBalanceResponseDto>

    @GET("wallet/transactions")
    suspend fun getWalletTransactions(): Response<WalletTransactionResponse>
}