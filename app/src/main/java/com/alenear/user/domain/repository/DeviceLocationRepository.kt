package com.alenear.user.domain.repository

import com.alenear.user.domain.model.UserAddress

interface DeviceLocationRepository {
    suspend fun getUserLocation(): UserAddress
    suspend fun getAddressFromLatLng(
        latitude: Double,
        longitude: Double
    ): UserAddress
}