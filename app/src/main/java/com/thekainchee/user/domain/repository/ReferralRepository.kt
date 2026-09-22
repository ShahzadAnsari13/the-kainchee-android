package com.thekainchee.user.domain.repository

import com.thekainchee.user.data.remote.dto.referral.ApplyReferralRequestDto
import com.thekainchee.user.data.remote.dto.referral.ApplyReferralResponseDto
import com.thekainchee.user.presentation.auth.model.ReferralResult
import com.thekainchee.user.presentation.referral.model.ReferralHistory

interface ReferralRepository {
    suspend fun applyReferral(
        referralCode: String,
        phoneNumber: String
    ): Result<ReferralResult>
    suspend fun getReferralHistory(): Result<ReferralHistory>
    suspend fun withdrawReferralReward(): Result<Int>
}