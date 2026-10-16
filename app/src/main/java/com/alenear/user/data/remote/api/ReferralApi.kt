package com.alenear.user.data.remote.api

import com.alenear.user.data.remote.dto.referral.ApplyReferralRequestDto
import com.alenear.user.data.remote.dto.referral.ApplyReferralResponseDto
import com.alenear.user.data.remote.dto.referral.ReferralHistoryResponseDto
import com.alenear.user.data.remote.dto.referral.WithdrawReferralResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReferralApi {
    @POST("referral/apply")
    suspend fun applyReferral(
        @Body request: ApplyReferralRequestDto
    ): Response<ApplyReferralResponseDto>

    @GET("referral/history")
    suspend fun getReferralHistory(): Response<ReferralHistoryResponseDto>

    @POST("referral/withdraw")
    suspend fun withdrawReferralReward(): Response<WithdrawReferralResponseDto>
}