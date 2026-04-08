package com.thekainchee.user.data.local.datastore

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    // Token
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

    //  Selected Address
    val SELECTED_ADDRESS_ID = stringPreferencesKey("selected_address_id")

    //  Current Location
    val CURRENT_LAT = doublePreferencesKey("current_lat")
    val CURRENT_LNG = doublePreferencesKey("current_lng")

}