package com.thekainchee.user.data.mapper

import android.location.Address
import com.thekainchee.user.data.local.room.entity.UserAddressEntity
import com.thekainchee.user.data.remote.dto.address.AddressDto
import com.thekainchee.user.data.remote.dto.address.AddressResponseData
import com.thekainchee.user.data.remote.dto.address.LocationDto
import com.thekainchee.user.data.remote.dto.address.ManualAddressDto
import com.thekainchee.user.data.remote.dto.address.UserAddressDto
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.presentation.location.model.AddressUI

fun UserAddress.toDto(): UserAddressDto {
    return UserAddressDto(
        label = label,
        isDefault = isDefault,
        location = LocationDto(
            coordinates = listOf(longitude, latitude), //  IMPORTANT (lng, lat)
            address = AddressDto(
                country = country,
                state = state,
                district = district,
                city = city,
                pincode = pincode
            ),
            manualAddress = ManualAddressDto(
                landmark = landmark,
                details = details
            )
        )
    )
}
fun AddressResponseData.toDomain(): UserAddress {
    return UserAddress(
        id = _id,
        label = label,
        latitude = location.coordinates[1],
        longitude = location.coordinates[0],
        country = location.address.country,
        state = location.address.state,
        district = location.address.district,
        city = location.address.city,
        pincode = location.address.pincode,
        landmark = location.manualAddress.landmark,
        details = location.manualAddress.details,
        isDefault = isDefault
    )
}

fun UserAddressEntity.toDomain(): UserAddress {
    return UserAddress(
        id = id,
        label = label,
        latitude = latitude,
        longitude = longitude,
        country = country,
        state = state,
        district = district,
        city = city,
        pincode = pincode,
        landmark = landmark,
        details = details,
        isDefault = isDefault
    )
}

fun Address.toDomain(
    latitude: Double,
    longitude: Double
): UserAddress {
    return UserAddress(
        id = "LIVE_LOCATION",
        label = "Other",
        latitude = latitude,
        longitude = longitude,
        country = countryName ?: "",
        state = adminArea ?: "",
        district = subAdminArea ?: "",
        city = locality ?: subAdminArea ?: "",
        pincode = postalCode ?: "",
        landmark = featureName
            ?.takeIf { it.isNotBlank() && it != "Unnamed Road" }
            ?: "",
        details = subLocality ?: "",
        isDefault = false
    )
}
fun AddressResponseData.toEntity(): UserAddressEntity {
    return UserAddressEntity(
        id = _id,
        label = label,
        latitude = location.coordinates[1],
        longitude = location.coordinates[0],
        country = location.address.country,
        state = location.address.state,
        district = location.address.district,
        city = location.address.city,
        pincode = location.address.pincode,
        landmark = location.manualAddress?.landmark,
        details = location.manualAddress?.details,
        isDefault = isDefault
    )
}

fun UserAddress.toUI(): AddressUI{
    return AddressUI(
        id = id,
        label = label,
        address = listOf(
            details,
            city,
            state,
            pincode
        )
            .filter { !it.isNullOrBlank() }
            .joinToString(", "),
        latitude = latitude,
        longitude = longitude,
        placeId = null,
        isSelected = isDefault,
        isFromSearch = false

    )
}