package com.thekainchee.user.presentation.booking.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemMyBookingBinding
import com.thekainchee.user.presentation.booking.adapter.MyBookingsAdapter.Companion.MyBookingsDiffCallback
import com.thekainchee.user.presentation.booking.model.MyBookingUiModel
import com.thekainchee.user.utils.DateFormatter

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
        Log.d("BOOKING", "Bind Position = $position")

        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(
        private val binding: ItemMyBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyBookingUiModel) = with(binding) {

            root.setOnClickListener {
                onItemClicked(item)
            }
            btnViewDetails.setOnClickListener {
                onItemClicked(item)
            }

            // Service
            tvServiceName.text = item.serviceName
            tvLocation.text = item.location
            // More Services
            if (item.serviceCount > 1) {
                tvMoreServices.isVisible = true
                tvMoreServices.text = "+${item.serviceCount - 1} more services"
            } else {
                tvMoreServices.isVisible = false
            }
            //image overlay service
            serviceCountContainer.isVisible = item.serviceCount > 0
            tvServiceCount.text =
                if (item.serviceCount == 1) {
                    "1 Service"
                } else {
                    "${item.serviceCount} Services"
                }

            // Parlour
            tvParlourName.text = item.parlourName

            // Staff
            tvStaffName.text = "with ${item.staffName}"

            // Amount
            tvAmount.text = "₹${item.totalPrice}"

            // Date
            tvDate.text =
                DateFormatter.formatBookingSuccessDate(item.bookingDate)

            // Time
            tvTime.text = item.slotStartTime

            // TODO : Glide
        Glide.with(ivService)
            .load(item.serviceImage)
            .into(ivService)

            // TODO : Status Color
            val (statusText, textColor, bgRes) = when (item.bookingStatus) {

                "PENDING" ->
                    Triple(
                        "Pending",
                        R.color.booking_pending,
                        R.drawable.bg_status_pending
                    )

                "CONFIRMED" ->
                    Triple(
                        "Confirmed",
                        R.color.booking_confirmed,
                        R.drawable.bg_status_confirmed
                    )

                "CHECKED_IN" ->
                    Triple(
                        "Checked In",
                        R.color.booking_checked_in,
                        R.drawable.bg_status_checked_in
                    )

                "COMPLETED" ->
                    Triple(
                        "Completed",
                        R.color.booking_completed,
                        R.drawable.bg_status_completed
                    )

                "CANCELLED" ->
                    Triple(
                        "Cancelled",
                        R.color.booking_cancelled,
                        R.drawable.bg_status_cancelled
                    )

                "NO_SHOW" ->
                    Triple(
                        "No Show",
                        R.color.booking_no_show,
                        R.drawable.bg_status_no_show
                    )

                else ->
                    Triple(
                        item.bookingStatus,
                        R.color.gray,
                        R.drawable.bg_status_default
                    )
            }

            binding.tvStatus.text = statusText
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(binding.root.context, textColor)
            )
            binding.tvStatus.setBackgroundResource(bgRes)
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