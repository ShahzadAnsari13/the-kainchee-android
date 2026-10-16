package com.alenear.user.data.mapper

import com.alenear.user.data.remote.dto.wallet.WalletTransactionDto
import com.alenear.user.presentation.wallet.model.WalletTransactionUiModel

fun WalletTransactionDto.toUiModel() = WalletTransactionUiModel(
    id = id,
    type = type,
    amount = amount,
    reason = reason,
    description = description,
    bookingId = bookingId,
    createdAt = createdAt
)