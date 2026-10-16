package com.alenear.user.domain.repository

import com.alenear.user.presentation.profile.model.ProfileUiModel

interface ProfileRepository {
    suspend fun getProfile(): Result<ProfileUiModel>
    suspend fun updateProfile(name: String): Result<String>

    suspend fun updateFcmToken(fcmToken: String): Result<String>
}