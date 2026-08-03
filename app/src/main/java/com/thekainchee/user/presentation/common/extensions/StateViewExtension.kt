package com.thekainchee.user.presentation.common.extensions

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.thekainchee.user.databinding.ViewStateBinding
import com.thekainchee.user.presentation.common.state.StateViewData

fun ViewStateBinding.show(data: StateViewData) {

    root.isVisible = true
    root.isClickable = true
    root.isFocusable = true
    imgState.setImageResource(data.image)

    tvTitle.text = data.title

    tvSubtitle.text = data.subtitle

    if (data.primaryButtonText != null) {

        btnPrimary.isVisible = true
        btnPrimary.text = data.primaryButtonText
        btnPrimary.setOnClickListener {
            data.onPrimaryClick?.invoke()
        }

    } else {
        btnPrimary.isGone = true
    }

    if (data.secondaryButtonText != null) {

        btnSecondary.isVisible = true
        btnSecondary.text = data.secondaryButtonText
        btnSecondary.setOnClickListener {
            data.onSecondaryClick?.invoke()
        }

    } else {
        btnSecondary.isGone = true
    }
}

fun ViewStateBinding.hide() {
    root.isGone = true
    btnPrimary.setOnClickListener(null)
    btnSecondary.setOnClickListener(null)
}