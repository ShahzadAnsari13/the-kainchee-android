package com.alenear.user.data.remote.dto.wallet

data class WalletTransactionResponse(val message: String,
                                     val data: List<WalletTransactionDto>)