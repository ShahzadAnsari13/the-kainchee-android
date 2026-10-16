package com.alenear.user.data.mapper

import com.alenear.user.data.remote.dto.referral.ReferralHistoryResponseDto
import com.alenear.user.presentation.referral.model.NextMilestone
import com.alenear.user.presentation.referral.model.ReferralHistory
import com.alenear.user.presentation.referral.model.ReferralItem

fun ReferralHistoryResponseDto.toDomain(): ReferralHistory {
    return ReferralHistory(
        referralCode = referralCode,
        totalReferrals = summary.totalReferrals,
        totalReferralRewards = summary.totalReferralRewards,
        currentReferrals = summary.currentReferrals,
        currentReferralRewards = summary.currentReferralRewards,
        nextMilestone = summary.nextMilestone?.let {
            NextMilestone(
                referralCount = it.referralCount,
                reward = it.reward
            )
        },
        referrals = referrals.map {
            ReferralItem(
                referredPhoneNumber = it.referredPhoneNumber,
                referralStatus = it.referralStatus,
                rewardAmount = it.rewardAmount,
                reason = it.reason,
                completedAt = it.completedAt,
                createdAt = it.createdAt
            )
        }
    )
}