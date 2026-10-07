package com.thekainchee.user.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemSearchResultBinding
import com.thekainchee.user.presentation.search.model.SearchParlourUiModel

class SearchResultAdapter(
    private var parlours: List<SearchParlourUiModel>,
    private val onParlourClick: (SearchParlourUiModel) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(parlour: SearchParlourUiModel) {

            binding.tvParlourName.text = parlour.name

            binding.tvRating.text = "★ ${parlour.rating}"
            binding.tvRatingCount.text = "(${parlour.ratingCount})"

            binding.tvParlourType.text = parlour.type

            binding.tvLocation.text = "⚑ ${parlour.distance} km | ${parlour.location}"
           // binding.tvLocation.text = "⚑ ${parlour.distance} km • ${parlour.location}"

            Glide.with(binding.ivParlourImage.context)
                .load(parlour.image)
                .placeholder(R.drawable.ic_oops)
                .error(R.drawable.error_img)
                .into(binding.ivParlourImage)

            binding.root.setOnClickListener {
                onParlourClick(parlour)
            }

            binding.ivArrow.setOnClickListener {
                onParlourClick(parlour)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultViewHolder {

        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SearchResultViewHolder,
        position: Int
    ) {
        holder.bind(parlours[position])
    }

    override fun getItemCount(): Int = parlours.size

    fun updateData(newParlours: List<SearchParlourUiModel>) {
        parlours = newParlours
        notifyDataSetChanged()
    }
}