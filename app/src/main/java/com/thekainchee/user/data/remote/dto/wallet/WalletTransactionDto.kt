package com.thekainchee.user.data.remote.dto.wallet

import com.google.gson.annotations.SerializedName

data class WalletTransactionDto(
    val id: String,
    val type: String,
    val amount: Double,
    val reason: String,
    val description : String,
    val bookingId: String?,
    val createdAt: String
)
