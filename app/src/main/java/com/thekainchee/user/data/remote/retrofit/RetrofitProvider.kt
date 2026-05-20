package com.thekainchee.user.data.remote.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    fun provide(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://172.20.253.101:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}