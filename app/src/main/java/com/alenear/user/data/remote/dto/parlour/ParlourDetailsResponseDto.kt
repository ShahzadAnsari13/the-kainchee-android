package com.alenear.user.data.remote.dto.parlour

data class ParlourDetailsResponseDto(val success: Boolean,
                                     val parlour: ParlourDetailedDto,
                                     val isOpenNow: Boolean)
