package com.thekainchee.user.data.remote.dto.parlour

import com.thekainchee.user.data.remote.dto.address.LocationDto

data class ParlourDetailedDto( val _id: String,
                               val name: String,
                               val images: List<String>,

                               val rating: RatingDto,

                               val description: String?,
                               val facilities: List<String>,

                               val workingHours: WorkingHoursDto,

                               val location: LocationDto,

                               val type: String,
                               val workersCount: Int,

                               val closeDay: List<String>,

                               val contactNumber: String )
