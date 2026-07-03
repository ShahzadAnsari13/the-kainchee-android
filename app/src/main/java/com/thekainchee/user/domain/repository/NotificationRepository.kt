package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.notification.model.NotificationUiModel

interface NotificationRepository {

    suspend fun getNotifications(): Result<List<NotificationUiModel>>

}