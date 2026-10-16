package com.alenear.user.data.remote.retrofit

import com.alenear.user.data.remote.interceptor.AuthInterceptor
import com.alenear.user.data.remote.interceptor.RefreshTokenAuthenticator
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpProvider {
    fun provide(loggingInterceptor: HttpLoggingInterceptor,authInterceptor: AuthInterceptor,refreshTokenAuthenticator: RefreshTokenAuthenticator) : OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(refreshTokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    fun provideNoAuth(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}