package com.alenear.user.data.remote.dto.Notification

data class NotificationResponse(
    val message: String,
    val data: List<NotificationDto>
)