package com.thekainchee.user.presentation.service.state

import com.thekainchee.user.presentation.service.model.BookingPreviewUiModel

sealed class BookingPreviewEvent {
    data class OpenBottomSheet (val data : BookingPreviewUiModel): BookingPreviewEvent()
    data class ShowToast(
        val message : String
    ) : BookingPreviewEvent()
}