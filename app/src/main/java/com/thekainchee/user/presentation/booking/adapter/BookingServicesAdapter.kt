package com.thekainchee.user.presentation.booking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemBookingServiceBinding
import com.thekainchee.user.presentation.booking.model.BookingServiceUiModel

class BookingServicesAdapter(
    private val services: List<BookingServiceUiModel>
) : RecyclerView.Adapter<BookingServicesAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(
        private val binding: ItemBookingServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(service: BookingServiceUiModel) {

            binding.tvServiceName.text = service.name

            binding.tvServiceDuration.text =
                "${service.durationMinutes} mins"

            binding.tvServicePrice.text =
                "₹${service.price.toInt()}"

            Glide.with(binding.ivServiceImage)
                .load(service.image)
                .placeholder(R.drawable.ic_oops)
                .into(binding.ivServiceImage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceViewHolder {

        val binding = ItemBookingServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ServiceViewHolder,
        position: Int
    ) {
        holder.bind(services[position])
    }

    override fun getItemCount(): Int = services.size
}