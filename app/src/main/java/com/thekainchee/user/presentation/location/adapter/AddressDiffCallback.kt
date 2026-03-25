package com.thekainchee.user.presentation.location.adapter

import androidx.recyclerview.widget.DiffUtil
import com.thekainchee.user.presentation.location.model.AddressUI

class AddressDiffCallback : DiffUtil.ItemCallback<AddressUI>() {
    override fun areItemsTheSame(
        oldItem: AddressUI,
        newItem: AddressUI
    ): Boolean {
        return oldItem.id == newItem.id

    }

    override fun areContentsTheSame(
        oldItem: AddressUI,
        newItem: AddressUI
    ): Boolean {
        return oldItem == newItem

    }

}