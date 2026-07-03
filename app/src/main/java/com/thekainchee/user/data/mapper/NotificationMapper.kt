package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.Notification.NotificationDto
import com.thekainchee.user.presentation.notification.model.NotificationUiModel

fun NotificationDto.toUiModel() = NotificationUiModel(
    id = id,
    title = title,
    body = body,
    type = type,
    isRead = isRead,
    createdAt = createdAt
)