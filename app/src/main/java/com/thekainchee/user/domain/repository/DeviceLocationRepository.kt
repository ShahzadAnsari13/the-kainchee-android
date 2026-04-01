package com.thekainchee.user.domain.repository

import com.thekainchee.user.domain.model.UserAddress

interface DeviceLocationRepository {
    suspend fun getUserLocation(): UserAddress
    suspend fun getAddressFromLatLng(
        latitude: Double,
        longitude: Double
    ): UserAddress
}