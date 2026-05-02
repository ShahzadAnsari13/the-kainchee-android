package com.thekainchee.user.presentation.dashboard.home.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemTrendingServiceBinding
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI

class TrendingServiceAdapter(private val onItemClick : (ServiceUI) -> Unit)
    : ListAdapter<ServiceUI, TrendingServiceAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object{
        private  val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ServiceUI>(){
            override fun areItemsTheSame(
                oldItem: ServiceUI,
                newItem: ServiceUI
            ): Boolean {
                return oldItem.serviceName == newItem.serviceName
            }

            override fun areContentsTheSame(
                oldItem: ServiceUI,
                newItem: ServiceUI
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding: ItemTrendingServiceBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item : ServiceUI){
            binding.tvServiceName.text = item.serviceName
            binding.tvPrice.text = "₹${item.avgPrice}"
            binding.tvDuration.text = "${item.avgDuration}min"
            binding.tvBookings.visibility =
                if (item.bookingCount >= 5) View.VISIBLE else View.GONE

            if (item.bookingCount >= 5) {
                binding.tvBookings.text ="${item.bookingCount}+ booked"
            }
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }


    override fun onBindViewHolder(holder: TrendingServiceAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrendingServiceAdapter.ViewHolder {
        val binding = ItemTrendingServiceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }


}