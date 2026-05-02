package com.thekainchee.user.presentation.location.state

sealed class AddressDeleteEvent {
    data class ShowMessage(val message: String) : AddressDeleteEvent()
}