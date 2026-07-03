package com.thekainchee.user.presentation.profile.state

sealed class EditProfileEvent {
    data class Success(val data :String) : EditProfileEvent()
    data class Error(val message : String) : EditProfileEvent()
}