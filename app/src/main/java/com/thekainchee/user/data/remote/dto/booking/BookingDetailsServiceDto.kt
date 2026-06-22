package com.thekainchee.user.data.remote.dto.booking

data class BookingDetailsServiceDto(val serviceId: String,
                                    val name: String,
                                    val price: Double,
                                    val durationMinutes: Int,
                                    val image: String?)
