package com.thekainchee.user.presentation.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.R
import com.thekainchee.user.databinding.BookingItemBinding
import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.utils.DateFormatter

class UpcomingBookingAdapter(private val onItemClick: (BookingUI) -> Unit) : ListAdapter<BookingUI, UpcomingBookingAdapter.ViewHolder>(DIFF_CALLBACK){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = BookingItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BookingUI>(){
            override fun areItemsTheSame(oldItem: BookingUI, newItem: BookingUI): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: BookingUI, newItem: BookingUI): Boolean {
                return oldItem == newItem
            }

        }
    }
    inner class ViewHolder(private val binding: BookingItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: BookingUI){
            binding.tvServices.text = item.serviceSummary
            val displayDate = DateFormatter.formatBookingDate(item.bookingDate)

            binding.tvDateTime.text =
                "$displayDate • ${item.slotStartTime} - ${item.slotEndTime}"
            binding.tvPrice.text = "₹${item.totalPrice}"
            val (textColor, bgRes) = when (item.bookingStatus) {
                "PENDING" -> Pair("#F9A825", R.drawable.bg_status_pending)
                "CONFIRMED" -> Pair("#2E7D32", R.drawable.bg_status_confirmed)
                else -> Pair("#757575", R.drawable.bg_status_default)
            }

            binding.tvStatus.text = item.bookingStatus

            binding.tvStatus.setTextColor(android.graphics.Color.parseColor(textColor))
            binding.tvStatus.setBackgroundResource(bgRes)
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

    }

}