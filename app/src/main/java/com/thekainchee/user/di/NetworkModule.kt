package com.thekainchee.user.di

import com.thekainchee.user.data.remote.api.AuthApi
import com.thekainchee.user.data.remote.interceptor.AuthInterceptor
import com.thekainchee.user.data.remote.interceptor.RefreshTokenAuthenticator
import com.thekainchee.user.data.remote.retrofit.LoggingInterceptorProvider
import com.thekainchee.user.data.remote.retrofit.OkHttpProvider
import com.thekainchee.user.data.remote.retrofit.RetrofitProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor() : HttpLoggingInterceptor{
        return LoggingInterceptorProvider.provide()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor,authInterceptor: AuthInterceptor,refreshTokenAuthenticator: RefreshTokenAuthenticator) : OkHttpClient{
        return OkHttpProvider.provide(loggingInterceptor,authInterceptor,refreshTokenAuthenticator)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitProvider.provide(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit) : AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}