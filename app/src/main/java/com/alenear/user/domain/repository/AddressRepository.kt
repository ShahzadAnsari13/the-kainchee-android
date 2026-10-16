package com.alenear.user.domain.repository

import com.alenear.user.domain.model.UserAddress
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    suspend fun addAddress(address: UserAddress)
    fun getAddresses(): Flow<List<UserAddress>>
    suspend fun refreshAddresses()

    suspend fun updateAddress(
        id: String,
        address: UserAddress
    )

    suspend fun deleteAddress(id:String)

    suspend fun setDefaultAddress(id:String)
}


