package com.thekainchee.user.data.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object TokenKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

}