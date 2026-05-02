package com.thekainchee.user.data.remote.interceptor

import com.thekainchee.user.data.local.datastore.UserPreferencesManager
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
    private val tokenManager: UserPreferencesManager,
    private val authApi: dagger.Lazy<AuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        synchronized(this) {

            if (response.code == 401) {
                val errorBody = response.peekBody(2048).string()

                if (!errorBody.contains("TOKEN_EXPIRED")) {
                    return null
                }

                val refreshToken = runBlocking { tokenManager.refreshToken.first() }

                if (refreshToken == null) {
                    runBlocking { tokenManager.clearTokens() }
                    return null
                }
            }

            val authHeader = response.request.header("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer"))
                return null

            if (responseCount(response) >= 2) {
                return null
            }

            val refreshToken = runBlocking {
                tokenManager.refreshToken.first()
            } ?: return null

            val newTokenResponse = runBlocking {
                authApi.get().refreshToken(
                    RefreshTokenRequestDto(refreshToken)
                )
            }

            if (!newTokenResponse.isSuccessful) {
                runBlocking { tokenManager.clearTokens() }
                return null
            }

            val newAccessToken =
                newTokenResponse.body()?.accessToken ?: return null

            runBlocking {
                tokenManager.saveTokens(newAccessToken, refreshToken)
            }

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