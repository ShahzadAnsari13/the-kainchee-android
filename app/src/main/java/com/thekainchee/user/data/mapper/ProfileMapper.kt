package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.profile.ProfileDto
import com.thekainchee.user.presentation.profile.model.ProfileUiModel

fun ProfileDto.toUI() : ProfileUiModel{
    return ProfileUiModel(
        name = this.name,
        countryCode = this.countryCode,
        phoneNumber = this.phoneNumber,
        walletBalance = this.walletBalance,
        notificationsEnabled = this.notificationsEnabled,
        isActive = this.isActive,
        memberSince = this.memberSince
    )
}