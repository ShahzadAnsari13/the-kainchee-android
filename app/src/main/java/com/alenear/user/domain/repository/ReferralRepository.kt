package com.alenear.user.domain.repository

import com.alenear.user.presentation.auth.model.ReferralResult
import com.alenear.user.presentation.referral.model.ReferralHistory

interface ReferralRepository {
    suspend fun applyReferral(
        referralCode: String,
        phoneNumber: String
    ): Result<ReferralResult>
    suspend fun getReferralHistory(): Result<ReferralHistory>
    suspend fun withdrawReferralReward(): Result<Int>
}