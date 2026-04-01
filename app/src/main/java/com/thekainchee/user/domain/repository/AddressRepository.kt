package com.thekainchee.user.domain.repository

import com.thekainchee.user.domain.model.UserAddress

interface AddressRepository {
    suspend fun addAddress(address: UserAddress): UserAddress
}