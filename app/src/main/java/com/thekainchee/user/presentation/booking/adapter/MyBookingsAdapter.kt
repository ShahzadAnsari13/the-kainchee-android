package com.thekainchee.user.presentation.booking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemMyBookingBinding
import com.thekainchee.user.presentation.booking.adapter.MyBookingsAdapter.Companion.MyBookingsDiffCallback
import com.thekainchee.user.presentation.booking.model.MyBookingUiModel

class MyBookingsAdapter(private val onItemClicked: (MyBookingUiModel) -> Unit) : ListAdapter<MyBookingUiModel, MyBookingsAdapter.BookingViewHolder>(MyBookingsDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookingViewHolder {
        val binding = ItemMyBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BookingViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(
        private val binding: ItemMyBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyBookingUiModel) {

            binding.root.setOnClickListener {
                onItemClicked(item)
            }

            // Binding next session
        }
    }


    companion object {

        private val MyBookingsDiffCallback =
            object : DiffUtil.ItemCallback<MyBookingUiModel>() {

                override fun areItemsTheSame(
                    oldItem: MyBookingUiModel,
                    newItem: MyBookingUiModel
                ): Boolean {
                    return oldItem.bookingId == newItem.bookingId
                }

                override fun areContentsTheSame(
                    oldItem: MyBookingUiModel,
                    newItem: MyBookingUiModel
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}