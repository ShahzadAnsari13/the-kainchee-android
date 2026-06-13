package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.wallet.WalletBalanceResponseDto
import retrofit2.Response

import retrofit2.http.GET

interface WalletApi {

    @GET("api/wallet/balance")
    suspend fun getWalletBalance(): Response<WalletBalanceResponseDto>

}