package com.alenear.user.presentation.location.state

sealed class AddressState {
    object Idle : AddressState()
    object Loading : AddressState()
}