package com.thekainchee.user.data.repository

import com.thekainchee.user.data.mapper.toDomain
import com.thekainchee.user.data.mapper.toDto
import com.thekainchee.user.data.remote.api.AddressApi
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.AddressRepository
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressApi: AddressApi
) : AddressRepository {
    override suspend fun addAddress(address: UserAddress): UserAddress {
        val dto = address.toDto()
        val response = addressApi.addAddress(dto)

        return response.address.toDomain() // 🔥 yahi hona chahiye
    }
}