package com.alenear.user.data.mapper

import com.alenear.user.data.remote.dto.profile.ProfileDto
import com.alenear.user.presentation.profile.model.ProfileUiModel

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