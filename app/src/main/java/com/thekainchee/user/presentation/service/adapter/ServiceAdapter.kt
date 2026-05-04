package com.thekainchee.user.presentation.service.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemServiceBinding
import com.thekainchee.user.presentation.service.model.ServiceUiModel

class ServiceAdapter(
    private val onAddClick: (ServiceUiModel) -> Unit
) : ListAdapter<ServiceUiModel, ServiceAdapter.ServiceViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ServiceUiModel>() {

            override fun areItemsTheSame(
                oldItem: ServiceUiModel,
                newItem: ServiceUiModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ServiceUiModel,
                newItem: ServiceUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ServiceViewHolder(
        private val binding: ItemServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServiceUiModel) {
            binding.apply {

                tvName.text = item.name
                tvPrice.text = "₹${item.price}"
                tvInfo.text = "${item.duration} min • ${item.description ?: ""}"

                if (item.isAvailable) {
                    btnAdd.isEnabled = true
                    root.alpha = 1f
                    tvUnavailable.visibility = View.GONE
                } else {
                    btnAdd.isEnabled = false
                    root.alpha = 0.5f
                    tvUnavailable.visibility = View.VISIBLE
                }

                if (item.isAdded) {
                    btnAdd.text = "ADDED ✓"
                    btnAdd.isEnabled = false
                } else {
                    btnAdd.text = "ADD"
                    btnAdd.isEnabled = true
                }

                btnAdd.setOnClickListener {
                    if (item.isAvailable && !item.isAdded) {
                        onAddClick(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}