package com.thekainchee.user.presentation.referral.model

data class ReferralHistory(
    val referralCode: String,
    val totalReferrals: Int,
    val totalReferralRewards: Int,
    val currentReferrals: Int,
    val currentReferralRewards: Int,
    val nextMilestone: NextMilestone?,
    val referrals: List<ReferralItem>
)
data class NextMilestone(
    val referralCount: Int,
    val reward: Int
)
data class ReferralItem(
    val referredPhoneNumber: String,
    val referralStatus: String,
    val rewardAmount: Int,
    val reason: String?,
    val completedAt: String?,
    val createdAt: String
)