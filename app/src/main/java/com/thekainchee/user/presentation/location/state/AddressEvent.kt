package com.thekainchee.user.presentation.location.state

sealed class AddressEvent {
    data class NavigateBack(val message : String)  : AddressEvent()
    data class ShowMessage(val message: String) : AddressEvent()
}