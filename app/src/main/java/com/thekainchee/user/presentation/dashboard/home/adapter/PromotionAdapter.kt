package com.thekainchee.user.presentation.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemPromotionBannerBinding

class PromotionAdapter(
    private val promotions: List<Int>
) : RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder>() {

    inner class PromotionViewHolder(
        private val binding: ItemPromotionBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageRes: Int) {
            binding.imgPromotion.setImageResource(imageRes)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PromotionViewHolder {

        val binding = ItemPromotionBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PromotionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PromotionViewHolder,
        position: Int
    ) {
        holder.bind(promotions[position])
    }

    override fun getItemCount(): Int = promotions.size
}