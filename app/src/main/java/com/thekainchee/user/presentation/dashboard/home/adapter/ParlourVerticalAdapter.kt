package com.thekainchee.user.presentation.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemParlourVerticalBinding
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI

class ParlourVerticalAdapter(
    private val onItemClick: (ParlourUI) -> Unit
) : ListAdapter<ParlourUI, ParlourVerticalAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemParlourVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ParlourUI) {

            binding.tvName.text = item.name
            binding.tvType.text =  item.type.uppercase()
            binding.tvRating.text = "⭐ ${item.rating}"
            binding.tvDistance.text =  "📍 ${item.distance} km away"

            // Image load
            Glide.with(binding.root.context)
                .load(item.image)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(binding.imgParlour)

            // Click
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
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
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParlourVerticalAdapter.ViewHolder {
        val binding = ItemParlourVerticalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParlourVerticalAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}