package com.thekainchee.user.presentation.location.state

import com.thekainchee.user.domain.model.UserAddress

sealed class MapState {
    object Idle : MapState()
    object Loading : MapState()

    data class LocationReceived(val lat: Double, val lng: Double) : MapState()
    data class AddressReceived(val address: UserAddress) : MapState()

    data class Error(val message: String) : MapState()
}