package com.alenear.user

import android.app.Application
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.alenear.user.utils.socket.SocketManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AleNearApp : Application() {
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
        lateinit var instance: AleNearApp
            private set
    }
}