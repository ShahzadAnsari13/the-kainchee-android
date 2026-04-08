package com.thekainchee.user.domain.repository

import com.thekainchee.user.data.local.room.entity.UserAddressEntity
import com.thekainchee.user.domain.model.UserAddress
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    suspend fun addAddress(address: UserAddress): UserAddress
    fun getAddresses(): Flow<List<UserAddress>>
    suspend fun refreshAddresses()

    suspend fun updateAddress(
        id: String,
        address: UserAddress
    )

    suspend fun deleteAddress(id:String)

    suspend fun setDefaultAddress(id:String)
}


