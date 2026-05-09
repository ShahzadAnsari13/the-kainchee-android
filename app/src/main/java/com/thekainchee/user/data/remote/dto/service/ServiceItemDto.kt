package com.thekainchee.user.data.remote.dto.service

data class ServiceItemDto(val _id : String,
                          val name: String,
                          val price: Double,
                          val durationMinutes: Int,
                          val description: String?,
                          val isAvailable: Boolean)