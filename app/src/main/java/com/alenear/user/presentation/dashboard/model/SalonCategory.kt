package com.alenear.user.presentation.dashboard.model

import com.alenear.user.presentation.dashboard.fragment.HomeFragment

data class SalonCategory(
    val name: String,
    val imageRes: Int,
    val type: HomeFragment.SalonCategoryType
)