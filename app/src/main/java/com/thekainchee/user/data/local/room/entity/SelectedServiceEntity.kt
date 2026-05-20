package com.thekainchee.user.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_services")
data class SelectedServiceEntity(
    @PrimaryKey
    val serviceId :String)