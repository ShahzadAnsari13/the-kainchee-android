package com.thekainchee.user.presentation.location.state

import com.thekainchee.user.domain.model.UserAddress

sealed class  AddressListState {
    object Idle : AddressListState()
    object Loading : AddressListState()
    data class Success(val data: List<UserAddress>) : AddressListState()
    data class Error(val message: String) : AddressListState()
}