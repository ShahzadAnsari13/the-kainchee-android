package com.thekainchee.user.data.remote.interceptor

import com.thekainchee.user.data.local.datastore.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.accessToken.first()
        }
        val request = if(token!=null){
            chain.request().newBuilder()
                .addHeader("Authorization","Bearer $token")
                .build()
        }else{
            chain.request()
        }
        return chain.proceed(request)
    }

}