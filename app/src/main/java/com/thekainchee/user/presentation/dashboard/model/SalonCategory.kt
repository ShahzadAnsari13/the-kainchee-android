package com.thekainchee.user.presentation.dashboard.model

import com.thekainchee.user.presentation.dashboard.fragment.HomeFragment

data class SalonCategory(
    val name: String,
    val imageRes: Int,
    val type: HomeFragment.SalonCategoryType
)