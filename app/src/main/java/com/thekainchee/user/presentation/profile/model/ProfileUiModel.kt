package com.thekainchee.user.presentation.profile.model

data class ProfileUiModel(val name: String,
                          val countryCode: String,
                          val phoneNumber: String,
                          val walletBalance: Double,
                          val notificationsEnabled: Boolean,
                          val isActive: Boolean,
                          val memberSince: String)
