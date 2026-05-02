package com.thekainchee.user.presentation.parlour.model

data class ParlourDetailedUI(   val id: String,
                                val name: String,
                                val images: List<String>,
                                val ratingAverage: Double,
                                val type : String,
                                val workersCount :Int,
                                val facilities: List<String>,
                                val ratingCount: Int,
                                val description: String?,
                                val location: LocationUiModel,
                                val openTime: String,
                                val closeTime: String,
                                val isOpenNow: Boolean,
                                val closeDay: List<String>,
                                val contactNumber: String)
