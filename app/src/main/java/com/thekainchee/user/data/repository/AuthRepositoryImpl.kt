package com.thekainchee.user.data.repository

import com.thekainchee.user.data.local.datastore.UserPreferencesManager
import com.thekainchee.user.data.local.room.dao.UserDao
import com.thekainchee.user.data.local.room.entity.UserEntity
import com.thekainchee.user.data.remote.api.AuthApi
import com.thekainchee.user.data.remote.dto.auth.CommonMessageDto
import com.thekainchee.user.data.remote.dto.auth.RequestOtpDto
import com.thekainchee.user.data.remote.dto.auth.VerifyOtpDto
import com.thekainchee.user.data.remote.dto.auth.VerifyOtpResponseDto
import com.thekainchee.user.domain.repository.AuthRepository
import com.thekainchee.user.utils.ErrorUtils
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi, private val tokenManager: UserPreferencesManager, private val userDao: UserDao) : AuthRepository{
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


}