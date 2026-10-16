package com.alenear.user.presentation.booking.state

import com.alenear.user.data.remote.dto.booking.RatingStatusDto

sealed class RatingStatusState {

    object Idle : RatingStatusState()

    object Loading : RatingStatusState()

    data class NotRated(
        val data: RatingStatusDto
    ) : RatingStatusState()

    data class AlreadyRated(
        val data: RatingStatusDto
    ) : RatingStatusState()
    object Submitted : RatingStatusState()

    data class Error(
        val message: String
    ) : RatingStatusState()
}