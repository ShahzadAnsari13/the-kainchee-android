package com.thekainchee.user.presentation.notification.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemNotificationBinding
import com.thekainchee.user.presentation.notification.model.NotificationUiModel

class NotificationAdapter : ListAdapter<NotificationUiModel, NotificationAdapter.NotificationViewHolder>(DiffCallback()) {

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationUiModel) = with(binding) {

            tvTitle.text = item.title
            tvBody.text = item.body
            tvTime.text = item.createdAt

            when (item.type) {

                "BOOKING_CONFIRMED" -> {
                    ivIcon.setImageResource(R.drawable.ic_calendar)
                    ivIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(root.context, R.color.success_green)
                    )
                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.success_green_light)
                    )
                }

                "BOOKING_CANCELLED" -> {
                    ivIcon.setImageResource(R.drawable.ic_cancel)
                    ivIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(root.context, R.color.error_red)
                    )
                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.error_red_light)
                    )
                }

                "PAYMENT_SUCCESS",
                "WALLET_CREDIT" -> {
                    ivIcon.setImageResource(R.drawable.ic_wallet_vect)
                    ivIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(root.context, R.color.primaryColor)
                    )
                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.light_green_background)
                    )
                }

                "PAYMENT_FAILED",
                "WALLET_DEBIT",
                "REFUND" -> {
                    ivIcon.setImageResource(R.drawable.ic_payment)
                    ivIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(root.context, R.color.orange)
                    )
                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.orange_light)
                    )
                }

                "PROMOTION" -> {
                    ivIcon.setImageResource(R.drawable.ic_offer)
                    ivIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(root.context, R.color.purple)
                    )
                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.purple_light)
                    )
                }

                "REMINDER",
                "SYSTEM" -> {
                    ivIcon.setImageResource(R.drawable.ic_notification)
                    ivIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(root.context, R.color.blue)
                    )
                    iconContainer.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.blue_light)
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationUiModel>() {

        override fun areItemsTheSame(
            oldItem: NotificationUiModel,
            newItem: NotificationUiModel
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: NotificationUiModel,
            newItem: NotificationUiModel
        ) = oldItem == newItem
    }
}