package com.thekainchee.user.presentation.common.model

data class ApiError(
    val message: String?,
    val code: String? = null
)