package com.thekainchee.user.presentation.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.data.local.datastore.TokenManager
import com.thekainchee.user.domain.repository.AuthRepository
import com.thekainchee.user.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val  authRepository: AuthRepository, private val tokenManager: TokenManager) : ViewModel(){
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun requestOtp(countryCode: String, phone: String){

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authRepository.requestOtp(countryCode, phone)
            if (result.isSuccess) {

                _authState.value = AuthState.OtpSent(
                    message = result.getOrNull()?.message ?: ""
                )

            } else {

                _authState.value = AuthState.Error(
                    message = result.exceptionOrNull()?.message ?: "OTP request failed"
                )
            }
        }
    }

    fun verifyOtp(countryCode: String,phone: String,otp: String){
        viewModelScope.launch {

            _authState.value = AuthState.Loading

            val result = authRepository.verifyOtp(countryCode,phone,otp)

            if(result.isSuccess){

                val data = result.getOrNull()

                if (data != null) {

                    tokenManager.saveTokens(data.accessToken, data.refreshToken)

                    _authState.value = AuthState.OtpVerified(
                        message = data.message,
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken
                    )

                } else {
                    _authState.value = AuthState.Error("Invalid response")
                }

            }else{

                _authState.value = AuthState.Error(   //  ye missing tha
                    message = result.exceptionOrNull()?.message ?: "Invalid OTP"
                )
            }
        }
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}