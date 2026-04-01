package com.thekainchee.user.presentation.location

import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.presentation.auth.AuthState

sealed class AddressState {
    object Idle : AddressState()
    object Loading : AddressState()
    data class Success(val message: String, val address: UserAddress) : AddressState()
    data class Error(val message: String) : AddressState()
}