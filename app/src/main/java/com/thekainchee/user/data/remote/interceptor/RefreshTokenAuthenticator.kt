package com.thekainchee.user.data.remote.interceptor

import com.thekainchee.user.data.local.datastore.TokenManager
import com.thekainchee.user.data.remote.api.AuthApi
import com.thekainchee.user.data.remote.dto.auth.RefreshTokenRequestDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class RefreshTokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: dagger.Lazy<AuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        synchronized(this) {
            // agar Authorization header nahi hai to refresh mat karo
            if (response.request.header("Authorization") == null)
                return null

            // infinite loop protection
            if (responseCount(response) >= 2) {
                return null
            }

            // get refresh token
            val refreshToken = runBlocking {
                tokenManager.refreshToken.first()
            } ?: return null

            // call refresh API
            val newTokenResponse = runBlocking {
                authApi.get().refreshToken(
                    RefreshTokenRequestDto(refreshToken)
                )
            }

            // refresh failed → logout
            if (!newTokenResponse.isSuccessful) {

                runBlocking {
                    tokenManager.clearTokens()
                }

                return null
            }

            val newAccessToken =
                newTokenResponse.body()?.accessToken ?: return null

            // save new access token
            runBlocking {
                tokenManager.saveTokens(newAccessToken, refreshToken)
            }

            // retry original request with new token
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse

        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }

        return result
    }
}