package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.parlour.BookingDto
import com.thekainchee.user.data.remote.dto.service.CategoryDto
import com.thekainchee.user.data.remote.dto.parlour.ParlourDetailsResponseDto
import com.thekainchee.user.data.remote.dto.parlour.ParlourDto
import com.thekainchee.user.data.remote.dto.service.ServiceCategoryDto
import com.thekainchee.user.data.remote.dto.parlour.ServiceDto
import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI
import com.thekainchee.user.presentation.parlour.model.LocationUiModel
import com.thekainchee.user.presentation.parlour.model.ParlourDetailedUI
import com.thekainchee.user.presentation.service.model.ServiceCategory

fun ParlourDto.toUI() : ParlourUI {
    return ParlourUI(
        id = _id,
        name = name,
        image = images?.firstOrNull(),
        rating = rating.average,
        distance = distance,
        type = type
    )
}

fun ServiceDto.toUI() : ServiceUI {
    return ServiceUI(
        serviceName = serviceName,
        bookingCount = bookingCount,
        avgPrice = avgPrice,
        avgDuration = avgDuration,
        image = image
    )

}

fun BookingDto.toUI() : BookingUI {
    val summary = when (services.size) {
        0 -> ""
        1 -> services[0].name
        2 -> "${services[0].name} & ${services[1].name}"
        else -> "${services[0].name} + ${services.size - 1} more"
    }
    return BookingUI(
        id = _id,
        serviceSummary = summary,
        image = services.firstOrNull()?.image.orEmpty(),
        totalPrice = totalPrice,
        totalDurationMinutes = totalDurationMinutes,
        bookingDate = bookingDate,
        slotStartTime = slotStartTime,
        slotEndTime = slotEndTime,
        bookingStatus = bookingStatus
    )
}

fun ParlourDetailsResponseDto.toUi() : ParlourDetailedUI{
    val p = this.parlour

    val coordinates = p.location.coordinates

    return ParlourDetailedUI(
        id = p._id,

        name = p.name,
        images = p.images,

        ratingAverage = p.rating.average,
        ratingCount = p.rating.count,

        description = p.description,
        workersCount = p.workersCount,
        facilities = p.facilities,
        location = LocationUiModel(
            latitude = coordinates[1],   // ⚠️ lat
            longitude = coordinates[0], // ⚠️ lng

            country = p.location.address.country,
            state = p.location.address.state,
            district = p.location.address.district,
            city = p.location.address.city,
            pincode = p.location.address.pincode,

            landmark = p.location.manualAddress?.landmark,
            details = p.location.manualAddress?.details
        ),

        openTime = p.workingHours.open,
        closeTime = p.workingHours.close,
        type = p.type,
        isOpenNow = this.isOpenNow,
        closeDay = p.closeDay,

        contactNumber = p.contactNumber
    )
}



