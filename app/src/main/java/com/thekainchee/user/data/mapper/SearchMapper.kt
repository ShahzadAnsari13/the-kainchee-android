package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.search.SearchParlourDto
import com.thekainchee.user.data.remote.dto.search.SearchResponse
import com.thekainchee.user.presentation.search.model.SearchParlourUiModel

fun SearchParlourDto.toUI() : SearchParlourUiModel{

    val address = location.address
    val landmark = location.manualAddress?.landmark

    val locationText = listOfNotNull(
        landmark,
        address.city ?: address.district
    ).joinToString(", ")

    return SearchParlourUiModel(
        id = _id,
        name = name,
        type = type,
        rating = rating.average,
        ratingCount = rating.count,
        image = images.firstOrNull(),
        location = locationText,
        distance = distance
    )
}