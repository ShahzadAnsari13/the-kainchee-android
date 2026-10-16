package com.alenear.user.data.repository

import com.alenear.user.data.mapper.toDomain
import com.alenear.user.data.remote.api.ReferralApi
import com.alenear.user.data.remote.dto.referral.ApplyReferralRequestDto
import com.alenear.user.domain.repository.ReferralRepository
import com.alenear.user.presentation.auth.model.ReferralResult
import com.alenear.user.presentation.referral.model.ReferralHistory
import com.alenear.user.utils.ErrorUtils
import javax.inject.Inject

class ReferralRepositoryImpl @Inject constructor(
    private val api: ReferralApi
) : ReferralRepository {

    override suspend fun applyReferral(
        referralCode: String,
        phoneNumber: String
    ): Result<ReferralResult> {

        return try {

            val request = ApplyReferralRequestDto(
                referralCode = referralCode,
                phoneNumber = phoneNumber
            )

            val response = api.applyReferral(request)

            if (response.isSuccessful && response.body() != null) {

                val data = response.body()!!

                Result.success(
                    ReferralResult(
                        message = data.message
                    )
                )

            } else {

                val errorBody = response.errorBody()?.string()
                val error = ErrorUtils.parseError(errorBody)

                Result.failure(
                    Exception(
                        error.message ?: "Failed to apply referral code"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override suspend fun getReferralHistory(): Result<ReferralHistory> {

        return try {
            val response = api.getReferralHistory()

            if (response.isSuccessful && response.body() != null) {
                Result.success(
                    response.body()!!.toDomain()
                )
            } else {
                val errorBody = response.errorBody()?.string()
                val error = ErrorUtils.parseError(errorBody)

                Result.failure(
                    Exception(
                        error.message ?: "Failed to fetch referral history"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun withdrawReferralReward(): Result<Int> {
        return try {
            val response = api.withdrawReferralReward()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.amount)
            } else {
                val errorBody = response.errorBody()?.string()
                val error = ErrorUtils.parseError(errorBody)

                Result.failure(
                    Exception(error.message ?: "Failed to withdraw referral reward")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}