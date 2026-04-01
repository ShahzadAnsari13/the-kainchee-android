package com.thekainchee.user.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val _isLoggedOut = MutableSharedFlow<Boolean>()
    val isLoggedOut = _isLoggedOut.asSharedFlow()
    suspend fun saveTokens(accessToken: String, refreshToken: String){
        context.dataStore.edit {prefs ->
            prefs[TokenKeys.ACCESS_TOKEN] = accessToken
            prefs[TokenKeys.REFRESH_TOKEN] = refreshToken
        }

    }

    val accessToken: Flow<String?>
        get() = context.dataStore.data.map { prefs ->
            prefs[TokenKeys.ACCESS_TOKEN]
        }
    val refreshToken: Flow<String?>
        get() = context.dataStore.data.map { prefs ->
            prefs[TokenKeys.REFRESH_TOKEN]
        }
    suspend fun clearTokens(){
        context.dataStore.edit {
            it.clear()
        }
        _isLoggedOut.emit(true)
    }
}