package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.profile.FcmTokenRequest
import com.thekainchee.user.data.remote.dto.profile.FcmTokenResponse
import com.thekainchee.user.data.remote.dto.profile.ProfileResponse
import com.thekainchee.user.data.remote.dto.profile.UpdateProfileRequest
import com.thekainchee.user.data.remote.dto.profile.UpdateProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

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
}