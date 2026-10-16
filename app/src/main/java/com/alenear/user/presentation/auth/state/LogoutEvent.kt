package com.alenear.user.presentation.auth.state

sealed class LogoutEvent {
    data object Success : LogoutEvent()
    data class Error(val message: String) : LogoutEvent()
}