package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.add_address.AddAddressResponseDto
import com.thekainchee.user.data.remote.dto.add_address.AddressDto
import com.thekainchee.user.data.remote.dto.add_address.AddressResponseData
import com.thekainchee.user.data.remote.dto.add_address.LocationDto
import com.thekainchee.user.data.remote.dto.add_address.ManualAddressDto
import com.thekainchee.user.data.remote.dto.add_address.UserAddressDto
import com.thekainchee.user.domain.model.UserAddress

fun UserAddress.toDto(): UserAddressDto {
    return UserAddressDto(
        label = label,
        isDefault = isDefault,
        location = LocationDto(
            coordinates = listOf(longitude, latitude), // 🔥 IMPORTANT (lng, lat)
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

