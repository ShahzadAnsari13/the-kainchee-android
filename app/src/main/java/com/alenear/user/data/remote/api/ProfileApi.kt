package com.alenear.user.data.remote.api

import com.alenear.user.data.remote.dto.auth.LogoutResponseDto
import com.alenear.user.data.remote.dto.profile.FcmTokenRequest
import com.alenear.user.data.remote.dto.profile.FcmTokenResponse
import com.alenear.user.data.remote.dto.profile.ProfileResponse
import com.alenear.user.data.remote.dto.profile.UpdateProfileRequest
import com.alenear.user.data.remote.dto.profile.UpdateProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ProfileApi {
    @GET("user/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PATCH("user/profile-update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @POST("user/fcm-token")
    suspend fun updateFcmToken(
        @Body request: FcmTokenRequest
    ): Response<FcmTokenResponse>

    @POST("auth/logout")
    suspend fun logout(
    ): Response<LogoutResponseDto>
}