package com.thekainchee.user.domain.repository

import com.thekainchee.user.domain.model.UserAddress

interface DeviceLocationRepository {
    suspend fun getUserLocation(): UserAddress
}