package com.alenear.user.data.mapper

import com.alenear.user.data.remote.dto.Notification.NotificationDto
import com.alenear.user.presentation.notification.model.NotificationUiModel

fun NotificationDto.toUiModel() = NotificationUiModel(
    id = id,
    title = title,
    body = body,
    type = type,
    isRead = isRead,
    createdAt = createdAt
)