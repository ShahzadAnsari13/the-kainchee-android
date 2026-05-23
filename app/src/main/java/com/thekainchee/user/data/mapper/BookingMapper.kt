package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.booking.StaffDto
import com.thekainchee.user.data.remote.dto.parlour.RatingDto
import com.thekainchee.user.presentation.booking.model.RatingModel
import com.thekainchee.user.presentation.booking.model.StaffUiModel

fun StaffDto.toUi() : StaffUiModel{
    return StaffUiModel(
        id = id,
        name = name,
        image = profileImage,
        phone = phone,
        rating = rating.toUi(),
        experience = experienceYears
    )
}

fun RatingDto.toUi(): RatingModel {
    return RatingModel(
        average = average.toFloat(),
        count = count
    )
}