package com.thekainchee.user.presentation.parlour.state

import com.thekainchee.user.presentation.parlour.model.ParlourDetailedUI

sealed class ParlourDetailedState {
    object Idle : ParlourDetailedState()
    object Loading : ParlourDetailedState()
    data class Success(val data: ParlourDetailedUI) : ParlourDetailedState()
    data class Error(val message : String) : ParlourDetailedState()
}