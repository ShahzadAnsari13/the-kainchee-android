package com.thekainchee.user.data.remote.dto.referral

data class ReferralHistoryResponseDto(
    val message: String,
    val referralCode: String,
    val summary: ReferralSummaryDto,
    val referrals: List<ReferralItemDto>
)
data class ReferralSummaryDto(
    val totalReferrals: Int,
    val totalReferralRewards: Int,
    val currentReferrals: Int,
    val currentReferralRewards: Int,
    val nextMilestone: NextMilestoneDto?
)

data class NextMilestoneDto(
    val referralCount: Int,
    val reward: Int
)

data class ReferralItemDto(
    val referredPhoneNumber: String,
    val referralStatus: String,
    val rewardAmount: Int,
    val reason: String?,
    val completedAt: String?,
    val createdAt: String
)