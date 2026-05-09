package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.service.CategoryDto
import com.thekainchee.user.data.remote.dto.service.ServiceItemDto
import com.thekainchee.user.presentation.service.model.ServiceCategory
import com.thekainchee.user.presentation.service.model.ServiceUiModel

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
        isAvailable = this.isAvailable
    )
}