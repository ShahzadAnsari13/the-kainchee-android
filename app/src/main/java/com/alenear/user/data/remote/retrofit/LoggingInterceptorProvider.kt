package com.alenear.user.data.remote.retrofit


import okhttp3.logging.HttpLoggingInterceptor
object LoggingInterceptorProvider {
    fun provide() : HttpLoggingInterceptor{
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}