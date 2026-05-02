package com.thekainchee.user.data.repository

import android.util.Log
import androidx.room.Transaction
import com.thekainchee.user.data.local.room.dao.UserAddressDao
import com.thekainchee.user.data.mapper.toDomain
import com.thekainchee.user.data.mapper.toDto
import com.thekainchee.user.data.mapper.toEntity
import com.thekainchee.user.data.remote.api.AddressApi
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.AddressRepository
import com.thekainchee.user.utils.ErrorUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressApi: AddressApi,
    private val userAddressDao: UserAddressDao
) : AddressRepository {
    override suspend fun addAddress(address: UserAddress) {

        val dto = address.toDto()
        val response = addressApi.addAddress(dto)
        if (response.isSuccessful) {
            val body = response.body() ?: throw Exception("Empty response")

            val entity = body.address.toEntity()
            userAddressDao.insertAddress(entity)

            if (entity.isDefault) {
                userAddressDao.updateDefault(entity.id)
            }

        } else {

            val error = ErrorUtils.parseError(response.errorBody()?.string())
            throw Exception(error.message ?: "Failed to add address")
        }
    }
    @Transaction
    override suspend fun refreshAddresses() {
        val response = addressApi.getAddresses()
        Log.d("API_DEBUG", "Response: ${response.body()}")
        if (response.isSuccessful) {
            val body = response.body() ?: throw Exception("Empty response")
            val entities = body.addresses.map { it.toEntity() }
            userAddressDao.clearAll()
            userAddressDao.insertAll(entities)
        }
        else {

            val error = ErrorUtils.parseError(response.errorBody()?.string())
            throw Exception("Refresh failed: ${error.message}")
        }
    }
    @Transaction
    override suspend fun updateAddress(
        id: String,
        address: UserAddress
    ) {
        val dto = address.toDto()

        val response = addressApi.updateAddress(id, dto)
        if (response.isSuccessful) {
            val body = response.body() ?: throw Exception("Empty response")
            val entity = body.address.toEntity()
            userAddressDao.insertAddress(entity)
        } else {

            val error = ErrorUtils.parseError(response.errorBody()?.string())
            throw Exception(error.message ?: "Update failed")
        }
    }

    override suspend fun deleteAddress(id: String) {
        val response = addressApi.deleteAddress(id)
        if (response.isSuccessful) {
            userAddressDao.deleteById(id)
        } else {

            val error = ErrorUtils.parseError(response.errorBody()?.string())
            throw Exception(error.message ?: "Failed to delete address")
        }
    }

    override suspend fun setDefaultAddress(id: String) {
        val response = addressApi.setDefaultAddress(id)
        if (response.isSuccessful) {
            userAddressDao.updateDefault(id)
        } else {

            val error = ErrorUtils.parseError(response.errorBody()?.string())
            throw Exception(error.message ?: "Something went wrong")
        }
    }

    override fun getAddresses(): Flow<List<UserAddress>> {
        return userAddressDao.getAllAddresses().map { entities ->
            entities.map { it.toDomain() }
        }
    }


}