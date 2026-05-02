package com.thekainchee.user.presentation.location.state

import com.thekainchee.user.domain.model.UserAddress

sealed class AddressState {
    object Idle : AddressState()
    object Loading : AddressState()
}