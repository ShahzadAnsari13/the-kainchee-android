package com.alenear.user.domain.repository

import com.alenear.user.presentation.notification.model.NotificationUiModel

interface NotificationRepository {

    suspend fun getNotifications(): Result<List<NotificationUiModel>>

}