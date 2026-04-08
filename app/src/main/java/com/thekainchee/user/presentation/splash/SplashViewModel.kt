package com.thekainchee.user.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.data.local.datastore.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SplashViewModel @Inject constructor(private val tokenManager: UserPreferencesManager) : ViewModel() {
    fun checkUserSession(onResult: (Boolean) -> Unit){
        viewModelScope.launch{
            delay(1500)
            val token = tokenManager.accessToken.first()
            onResult(!token.isNullOrEmpty())
        }
    }
}