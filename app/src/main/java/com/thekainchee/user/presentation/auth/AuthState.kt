package com.thekainchee.user.presentation.auth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class OtpSent(val message: String) : AuthState()
    data class OtpVerified(val message : String,val accessToken : String, val refreshToken : String) : AuthState()
    data class Error(val message: String?) : AuthState()
}
