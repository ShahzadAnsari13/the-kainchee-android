package com.thekainchee.user.presentation.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.databinding.ItemParlourHorizontalBinding
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI

class ParlourHorizontalAdapter(
    private val onItemClick: (ParlourUI) -> Unit
) : ListAdapter<ParlourUI, ParlourHorizontalAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ParlourUI>() {

            override fun areItemsTheSame(oldItem: ParlourUI, newItem: ParlourUI): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ParlourUI, newItem: ParlourUI): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding: ItemParlourHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ParlourUI) {

            binding.tvName.text = item.name
            binding.tvType.text = item.type
            binding.tvRating.text = "⭐ ${item.rating}"
            binding.tvDistance.text = "${item.distance} km"

            // Image load
            Glide.with(binding.root.context)
                .load(item.image)
                .into(binding.ivParlour)

            // Click
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemParlourHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}