package com.thekainchee.user.presentation.notification.model

data class NotificationUiModel(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)