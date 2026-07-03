package com.thekainchee.user.data.repository

import com.thekainchee.user.data.mapper.toUI
import com.thekainchee.user.data.remote.api.ProfileApi
import com.thekainchee.user.data.remote.dto.profile.FcmTokenRequest
import com.thekainchee.user.data.remote.dto.profile.UpdateProfileRequest
import com.thekainchee.user.domain.repository.ProfileRepository
import com.thekainchee.user.presentation.profile.model.ProfileUiModel
import com.thekainchee.user.utils.ErrorUtils
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val api: ProfileApi)  : ProfileRepository {
    override suspend fun getProfile(): Result<ProfileUiModel> {
        return try{
            val response =  api.getProfile()
            if(response.isSuccessful && response.body() != null){
                val data = response.body()?.data
                if(data != null){
                    Result.success(data.toUI())
                }else{
                    Result.failure(Exception("Failed to fetch profile"))
                }
            }else{
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to fetch profile"))
            }
        }
        catch(e : Exception){
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String): Result<String> {
        return try{
            val response = api.updateProfile(UpdateProfileRequest(name))
            if(response.isSuccessful && response.body() != null) {
                val message = response.body()!!.message
                Result.success(message)
            }
            else{
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to update profile"))
            }
        }
        catch(e : Exception){
            Result.failure(e)

        }
    }

    override suspend fun updateFcmToken(fcmToken: String): Result<String> {
        return try {
            val response = api.updateFcmToken(FcmTokenRequest(fcmToken))
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Success"
                Result.success(message)
            } else {
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(Exception(error.message ?: "Failed to update FCM token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}