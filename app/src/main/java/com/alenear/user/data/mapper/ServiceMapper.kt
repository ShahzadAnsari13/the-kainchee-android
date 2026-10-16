package com.alenear.user.data.mapper

import com.alenear.user.data.remote.dto.service.BookingPreviewResponseDto
import com.alenear.user.data.remote.dto.service.CategoryDto
import com.alenear.user.data.remote.dto.service.ServiceItemDto
import com.alenear.user.presentation.service.model.BookingPreviewItemUiModel
import com.alenear.user.presentation.service.model.BookingPreviewUiModel
import com.alenear.user.presentation.service.model.ServiceCategory
import com.alenear.user.presentation.service.model.ServiceUiModel
import kotlin.collections.map

fun CategoryDto.toUI(): ServiceCategory {
    return ServiceCategory(
        id = this._id,
        name = this.name,
        image = this.image
    )
}

fun ServiceItemDto.toUI() : ServiceUiModel{
    return ServiceUiModel(
        id = this._id,
        name = this.name,
        price = this.price,
        duration = this.durationMinutes,
        description = this.description,
        isAvailable = this.isAvailable,
        image = this.image
    )
}

fun BookingPreviewResponseDto.toUI()
        : BookingPreviewUiModel {

    return BookingPreviewUiModel(

        services = this.services.map { item ->

            BookingPreviewItemUiModel(
                id = item.id,
                name = item.name,

                price = item.price,

                duration = item.durationMinutes,

                image = item.image
            )
        },
        parlourId = this.parlourId,

        totalPrice = this.totalPrice,

        totalDuration = this.totalDuration,

        totalServices = this.totalServices
    )
}