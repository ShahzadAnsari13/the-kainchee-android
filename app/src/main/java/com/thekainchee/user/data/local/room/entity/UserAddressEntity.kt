package com.thekainchee.user.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "user_address")
data class UserAddressEntity(

    @PrimaryKey
    val id: String,

    val label: String, // Home, Work, Other

    val latitude: Double,
    val longitude: Double,

    val country: String,
    val state: String,
    val district: String,
    val city: String?,
    val pincode: String?,

    val landmark: String?,
    val details: String?,

    val isDefault: Boolean
)


