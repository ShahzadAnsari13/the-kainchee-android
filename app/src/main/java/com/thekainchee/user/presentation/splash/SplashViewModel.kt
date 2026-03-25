package com.thekainchee.user.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.data.local.datastore.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SplashViewModel @Inject constructor(private val tokenManager: TokenManager) : ViewModel() {
    fun checkUserSession(onResult: (Boolean) -> Unit){
        viewModelScope.launch{
            delay(1500)
            val token = tokenManager.accessToken.first()
            onResult(!token.isNullOrEmpty())
        }
    }
}