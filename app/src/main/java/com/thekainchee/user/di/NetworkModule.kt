package com.thekainchee.user.di

import com.thekainchee.user.data.remote.api.AddressApi
import com.thekainchee.user.data.remote.api.AuthApi
import com.thekainchee.user.data.remote.api.ParlourApi
import com.thekainchee.user.data.remote.api.ServiceApi
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
import javax.inject.Named
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
    @Named("NoAuth")
    fun provideNoAuthOkHttpClient(loggingInterceptor: HttpLoggingInterceptor) : OkHttpClient {
        return OkHttpProvider.provideNoAuth(loggingInterceptor)
    }
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitProvider.provide(okHttpClient)
    }

    @Provides
    @Singleton
    @Named("NoAuth")
    fun provideNoAuthRetrofit(
        @Named("NoAuth") okHttpClient: OkHttpClient
    ): Retrofit {
        return RetrofitProvider.provide(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("NoAuth") retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAddressApi(retrofit: Retrofit) : AddressApi {
        return retrofit.create(AddressApi::class.java)
    }

    @Provides
    @Singleton
    fun provideParlourApi(retrofit: Retrofit) : ParlourApi{
        return retrofit.create(ParlourApi::class.java)
    }

    @Provides
    @Singleton
    fun provideServiceApi(retrofit: Retrofit): ServiceApi{
        return retrofit.create(ServiceApi::class.java)
    }
}