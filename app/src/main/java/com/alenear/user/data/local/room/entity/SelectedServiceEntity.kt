package com.alenear.user.data.local.room.entity

import androidx.room.Entity

@Entity(tableName = "selected_services",
    primaryKeys = ["parlourId", "serviceId"]
)
data class SelectedServiceEntity(
    val parlourId: String,
    val serviceId :String)