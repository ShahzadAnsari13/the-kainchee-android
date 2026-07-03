package com.thekainchee.user.domain.repository

import com.thekainchee.user.presentation.profile.model.ProfileUiModel

interface ProfileRepository {
    suspend fun getProfile(): Result<ProfileUiModel>
    suspend fun updateProfile(name: String): Result<String>

    suspend fun updateFcmToken(fcmToken: String): Result<String>
}