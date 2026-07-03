package com.thekainchee.user.presentation.wallet.model

data class WalletTransactionUiModel(

    val id: String,

    val type: String,

    val amount: Double,

    val reason: String,
    val description : String,

    val bookingId: String?,

    val createdAt: String
)
