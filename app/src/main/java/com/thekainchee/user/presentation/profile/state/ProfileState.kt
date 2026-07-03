package com.thekainchee.user.presentation.profile.state

import com.thekainchee.user.presentation.profile.model.ProfileUiModel

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success (val data : ProfileUiModel) : ProfileState()
    data class Error(val message : String) : ProfileState()
}