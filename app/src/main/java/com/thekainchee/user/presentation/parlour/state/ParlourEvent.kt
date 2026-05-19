package com.thekainchee.user.presentation.parlour.state

sealed class ParlourEvent {
    data object NavigateToServices
        : ParlourEvent()

    data class ShowError(
        val message: String
    ) : ParlourEvent()

    data object ParlourClosed
        : ParlourEvent()
}