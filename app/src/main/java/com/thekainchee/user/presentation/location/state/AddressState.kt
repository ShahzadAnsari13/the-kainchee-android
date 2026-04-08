package com.thekainchee.user.presentation.location.state

import com.thekainchee.user.domain.model.UserAddress

sealed class AddressState {
    object Idle : AddressState()
    object Loading : AddressState()
    data class CreateAddress(val message: String, val address: UserAddress) : AddressState()
    data class UpdateAddress(val message: String) : AddressState()
    data class Error(val message: String) : AddressState()
}