package com.thekainchee.user.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val _isLoggedOut = MutableStateFlow(false)

    val isLoggedOut = _isLoggedOut.asStateFlow()
    suspend fun saveTokens(accessToken: String, refreshToken: String){
        context.dataStore.edit {prefs ->
            prefs[PreferencesKeys.ACCESS_TOKEN] = accessToken
            prefs[PreferencesKeys.REFRESH_TOKEN] = refreshToken
        }
        _isLoggedOut.value = false
    }

    val accessToken: Flow<String?>
        get() = context.dataStore.data
            .map { it[PreferencesKeys.ACCESS_TOKEN] }
            .distinctUntilChanged()

    val refreshToken: Flow<String?>
        get() = context.dataStore.data
            .map { it[PreferencesKeys.REFRESH_TOKEN] }
            .distinctUntilChanged()
    suspend fun clearTokens() {
        context.dataStore.edit {
            it.remove(PreferencesKeys.ACCESS_TOKEN)
            it.remove(PreferencesKeys.REFRESH_TOKEN)
        }
        _isLoggedOut.value = true
    }


    suspend fun saveSelectedAddressId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SELECTED_ADDRESS_ID] = id
        }
    }
    val selectedAddressId: Flow<String?>
        get() = context.dataStore.data
            .map { it[PreferencesKeys.SELECTED_ADDRESS_ID] }
            .distinctUntilChanged()

    suspend fun clearSelectedAddress() {
        context.dataStore.edit {
            it.remove(PreferencesKeys.SELECTED_ADDRESS_ID)
        }
    }

    suspend fun saveLocation(lat: Double, lng: Double) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.CURRENT_LAT] = lat
            prefs[PreferencesKeys.CURRENT_LNG] = lng
        }
    }


    val location: Flow<Pair<Double?, Double?>>
        get() = context.dataStore.data
            .map {
                Pair(
                    it[PreferencesKeys.CURRENT_LAT],
                    it[PreferencesKeys.CURRENT_LNG]
                )
            }
            .distinctUntilChanged()
}