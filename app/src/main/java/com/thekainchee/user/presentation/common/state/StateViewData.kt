package com.thekainchee.user.presentation.common.state

data class StateViewData(

    val image: Int,

    val title: String,

    val subtitle: String,

    val primaryButtonText: String? = null,

    val secondaryButtonText: String? = null,

    val onPrimaryClick: (() -> Unit)? = null,

    val onSecondaryClick: (() -> Unit)? = null
)
