package com.thekainchee.user.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.thekainchee.user.data.local.room.dao.UserAddressDao
import com.thekainchee.user.data.local.room.entity.UserAddressEntity
import com.thekainchee.user.data.location.DeviceLocationProvider
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.DeviceLocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class DeviceLocationRepositoryImpl @Inject constructor(
    private val userAddressDao: UserAddressDao,
    private val locationProvider: DeviceLocationProvider,
    @ApplicationContext private val context: Context
) : DeviceLocationRepository {

    private suspend fun getGeoData(
        latitude: Double,
        longitude: Double
    ): Address {

        val geocoder = Geocoder(context, Locale.getDefault())

        val result = withContext(Dispatchers.IO) {
            geocoder.getFromLocation(latitude, longitude, 1)
        }

        return result?.firstOrNull()
            ?: throw Exception("Address not found")
    }

    override suspend fun getUserLocation(): UserAddress {

        val address = userAddressDao.getDefaultAddress()
        Log.d("LocationDebug", "DB ADDRESS: $address")

        return if (address != null) {

            UserAddress(
                label = address.label,
                latitude = address.latitude,
                longitude = address.longitude,
                country = address.country,
                state = address.state,
                district = address.district,
                city = address.city,
                pincode = address.pincode,
                landmark = address.landmark,
                details = address.details,
                isDefault = address.isDefault
            )

        } else {

            val location = locationProvider.getCurrentLocation()
                ?: throw Exception("Location not available")

            val geo = getGeoData(location.latitude, location.longitude)

            val entity = UserAddressEntity(
                label = "Other",
                latitude = location.latitude,
                longitude = location.longitude,
                country = geo.countryName ?: "",
                state = geo.adminArea ?: "",
                district = geo.subAdminArea ?: "",
                city = geo.locality ?: geo.subAdminArea ?: "",
                pincode =  geo.postalCode ?: "",
                landmark = geo.featureName
                    ?.takeIf { it.isNotBlank() && it != "Unnamed Road" }
                    ?: "",
                details = geo.subLocality ?: "",
                isDefault = false
            )

            userAddressDao.deleteLiveLocation()
            userAddressDao.insertAddress(entity)

            UserAddress(
                label = entity.label,
                latitude = entity.latitude,
                longitude = entity.longitude,
                country = entity.country,
                state = entity.state,
                district = entity.district,
                city = entity.city,
                pincode = entity.pincode,
                landmark = entity.landmark,
                details = entity.details,
                isDefault = entity.isDefault
            )
        }
    }

    override suspend fun getAddressFromLatLng(
        latitude: Double,
        longitude: Double
    ): UserAddress {

        val geo = getGeoData(latitude, longitude)

        return UserAddress(
            label = "Other",
            latitude = latitude,
            longitude = longitude,
            country = geo.countryName ?: "",
            state = geo.adminArea ?: "",
            district = geo.subAdminArea ?: "",
            city = geo.locality ?: geo.subAdminArea ?: "",
            pincode =  geo.postalCode ?: "",
            landmark = geo.featureName
                ?.takeIf { it.isNotBlank() && it != "Unnamed Road" }
                ?: "",
            details =geo.subLocality ?: "",
            isDefault = false
        )
    }
}