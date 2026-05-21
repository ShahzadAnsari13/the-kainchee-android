package com.thekainchee.user.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_services",
    primaryKeys = ["parlourId", "serviceId"]
)
data class SelectedServiceEntity(
    val parlourId: String,
    val serviceId :String)