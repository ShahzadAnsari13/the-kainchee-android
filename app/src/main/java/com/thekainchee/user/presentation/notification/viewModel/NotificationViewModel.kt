package com.thekainchee.user.presentation.notification.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.NotificationRepository
import com.thekainchee.user.presentation.notification.state.NotificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {
    private val _notificationState = MutableStateFlow<NotificationState>(NotificationState.Idle)
    val notificationState: StateFlow<NotificationState> = _notificationState

    fun getNotifications() {

        viewModelScope.launch {

            _notificationState.value = NotificationState.Loading

            repository.getNotifications()
                .onSuccess { notifications ->
                    _notificationState.value = if (notifications.isEmpty()) {
                        NotificationState.Empty
                    } else {
                        NotificationState.Success(notifications)
                    }
                }
                .onFailure {
                    _notificationState.value = NotificationState.Error(
                        it.message ?: "Something went wrong"
                    )
                }
        }
    }
}