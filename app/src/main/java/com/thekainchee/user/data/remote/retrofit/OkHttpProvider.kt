package com.thekainchee.user.data.remote.retrofit

import com.thekainchee.user.data.remote.interceptor.AuthInterceptor
import com.thekainchee.user.data.remote.interceptor.RefreshTokenAuthenticator
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
}