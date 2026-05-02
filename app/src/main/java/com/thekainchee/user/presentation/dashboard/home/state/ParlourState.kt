package com.thekainchee.user.presentation.dashboard.home.state

import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI

sealed class ParlourState {

    object Idle : ParlourState()

    object Loading : ParlourState()

    data class Success(
        val data: List<ParlourUI>,val isPagination: Boolean = false) : ParlourState()

    data class Error(
        val message: String
    ) : ParlourState()
}