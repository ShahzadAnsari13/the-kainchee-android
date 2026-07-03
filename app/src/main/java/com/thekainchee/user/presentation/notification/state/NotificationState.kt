package com.thekainchee.user.presentation.notification.state

import com.thekainchee.user.presentation.notification.model.NotificationUiModel

sealed class NotificationState {

    data object Idle : NotificationState()

    data object Loading : NotificationState()

    data class Success(
        val notifications: List<NotificationUiModel>
    ) : NotificationState()

    data object Empty : NotificationState()

    data class Error(
        val message: String
    ) : NotificationState()
}