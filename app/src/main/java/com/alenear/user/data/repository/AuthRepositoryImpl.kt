package com.alenear.user.data.repository

import com.alenear.user.data.local.datastore.UserPreferencesManager
import com.alenear.user.data.local.room.dao.UserDao
import com.alenear.user.data.local.room.entity.UserEntity
import com.alenear.user.data.remote.api.AuthApi
import com.alenear.user.data.remote.api.ProfileApi
import com.alenear.user.data.remote.dto.auth.CommonMessageDto
import com.alenear.user.data.remote.dto.auth.RequestOtpDto
import com.alenear.user.data.remote.dto.auth.VerifyOtpDto
import com.alenear.user.domain.repository.AuthRepository
import com.alenear.user.utils.ErrorUtils
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val profileApi : ProfileApi,private val authApi: AuthApi, private val tokenManager: UserPreferencesManager, private val userDao: UserDao) : AuthRepository{
    override suspend fun requestOtp(
        countryCode: String,
        phone: String
    ): Result<CommonMessageDto> {
        return try {
            val response = authApi.requestOtp(
                RequestOtpDto(
                    countryCode = countryCode,
                    phone = phone
                )
            )
            if(response.isSuccessful && response.body() !=null){
                Result.success(response.body()!!)
            }else{

                val errorBody = response.errorBody()?.string()
                val errorMsg = ErrorUtils.parseError(errorBody)
                Result.failure(Exception(errorMsg.message))
            }
        }
        catch(e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(
        countryCode: String,
        phone: String,
        otp: String
    ): Result< Boolean>{
        return try{
            val response = authApi.verifyOtp(
                VerifyOtpDto(countryCode,phone,otp)
            )

            if(response.isSuccessful && response.body() != null){
                val body = response.body()!!

                // Token → DataStore
                tokenManager.saveTokens(
                    body.accessToken,
                    body.refreshToken
                )

                // Name validation
                val isNameSet =
                    body.name.isNotBlank() && body.name != "XYZ"

                // Valid name → Room
                if(isNameSet) {
                    userDao.insertOrReplace(
                        UserEntity(
                            id = 1,
                            name = body.name
                        )
                    )
                }
                Result.success(isNameSet)
            }else{
                val errorBody = response.errorBody()?.string()
                val errorMsg = ErrorUtils.parseError(errorBody)
                Result.failure(Exception(errorMsg.message))
            }
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try{
            val response = profileApi.logout()
            if(response.isSuccessful){
                Result.success(Unit)
            }else{
                val errorBody = response.errorBody()?.string()
                val errorMsg = ErrorUtils.parseError(errorBody)
                Result.failure(Exception(errorMsg.message))
            }
        }
        catch(e: Exception){
            Result.failure(e)
        }
    }


}