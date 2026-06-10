package com.thekainchee.user

import android.app.Application
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.thekainchee.user.utils.socket.SocketManager
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.Socket

@HiltAndroidApp
class TheKaincheeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        if(!Places.isInitialized()){
            Places.initialize(applicationContext,"AIzaSyCd-LFRoFR4VCJNkqYnq31Xn02XkWPzwXo")
            Log.d("PLACES_INIT", "Initialized: ${Places.isInitialized()}")
        }
        SocketManager.connect()

    }
    companion object {
        lateinit var instance: TheKaincheeApp
            private set
    }
}