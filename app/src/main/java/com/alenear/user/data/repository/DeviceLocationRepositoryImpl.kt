package com.alenear.user.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.alenear.user.data.local.datastore.UserPreferencesManager
import com.alenear.user.data.local.room.dao.UserAddressDao
import com.alenear.user.data.location.DeviceLocationProvider
import com.alenear.user.data.mapper.toDomain
import com.alenear.user.domain.model.UserAddress
import com.alenear.user.domain.repository.DeviceLocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class DeviceLocationRepositoryImpl @Inject constructor(
    private val userAddressDao: UserAddressDao,
    private val locationProvider: DeviceLocationProvider,
    private val preferencesManager: UserPreferencesManager,
    @ApplicationContext private val context: Context
) : DeviceLocationRepository {
    private val geocoder by lazy {
        Geocoder(context, Locale.getDefault())
    }


    private suspend fun getGeoData(
        latitude: Double,
        longitude: Double
    ): Address {

        return try {

            val result = withContext(Dispatchers.IO) {
                geocoder.getFromLocation(latitude, longitude, 1)
            }

            result?.firstOrNull()
                ?: throw Exception("Address not found")

        } catch (e: Exception) {
            Log.e("GeoCoder", "Error fetching address", e)
            throw Exception("Unable to fetch address")
        }
    }

    override suspend fun getUserLocation(): UserAddress {

        // 1. Selected address
        val selectedId = preferencesManager.selectedAddressId.firstOrNull()

        val selectedAddress = selectedId?.let {
            userAddressDao.getAddressById(it)
        }
        val address = selectedAddress ?: userAddressDao.getDefaultAddress()

        if (address != null) {
            return address.toDomain()

        }


        // 3. Live location
        val location = locationProvider.getCurrentLocation()
            ?:throw IllegalStateException("Unable to fetch address from coordinates")
        // DataStore me save pehle
        preferencesManager.saveLocation(location.latitude, location.longitude)

        val geo = getGeoData(location.latitude, location.longitude)

        return geo.toDomain(location.latitude, location.longitude)
    }

    override suspend fun getAddressFromLatLng(
        latitude: Double,
        longitude: Double
    ): UserAddress {

        val geo = getGeoData(latitude, longitude)

        return geo.toDomain(latitude, longitude)
    }
}