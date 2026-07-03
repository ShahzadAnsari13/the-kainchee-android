package com.thekainchee.user.data.remote.dto.wallet

import com.google.gson.annotations.SerializedName

data class WalletTransactionResponse(val message: String,
                                     val data: List<WalletTransactionDto>)