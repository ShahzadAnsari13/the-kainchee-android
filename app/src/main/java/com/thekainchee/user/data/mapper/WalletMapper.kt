package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.wallet.WalletTransactionDto
import com.thekainchee.user.presentation.wallet.model.WalletTransactionUiModel

fun WalletTransactionDto.toUiModel() = WalletTransactionUiModel(
    id = id,
    type = type,
    amount = amount,
    reason = reason,
    description = description,
    bookingId = bookingId,
    createdAt = createdAt
)