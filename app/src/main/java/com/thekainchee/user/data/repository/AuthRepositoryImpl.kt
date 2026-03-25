package com.thekainchee.user.data.repository

import com.thekainchee.user.data.remote.api.AuthApi
import com.thekainchee.user.data.remote.dto.auth.CommonMessageDto
import com.thekainchee.user.data.remote.dto.auth.RequestOtpDto
import com.thekainchee.user.data.remote.dto.auth.VerifyOtpDto
import com.thekainchee.user.data.remote.dto.auth.VerifyOtpResponseDto
import com.thekainchee.user.domain.repository.AuthRepository
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi) : AuthRepository{
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
                val errorMsg = parseError(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
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
    ): Result<VerifyOtpResponseDto> {
        return try{
            val response = authApi.verifyOtp(
                VerifyOtpDto(countryCode,phone,otp)
            )

            if(response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            }else{
                val errorMsg = parseError(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    private fun parseError(errorBody: String?): String {
        return try {
            val jsonObject = JSONObject(errorBody ?: "")
            jsonObject.optString("message", "Something went wrong")
        } catch (e: Exception) {
            "Something went wrong"
        }
    }
}