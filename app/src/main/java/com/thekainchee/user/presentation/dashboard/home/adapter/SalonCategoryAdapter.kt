package com.thekainchee.user.presentation.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.R
import com.thekainchee.user.databinding.LayoutParlourCategoryBinding
import com.thekainchee.user.presentation.dashboard.home.model.SalonCategory


class SalonCategoryAdapter(
    private val categories: List<SalonCategory>,
    private val onCategorySelected: (SalonCategory) -> Unit
) : RecyclerView.Adapter<SalonCategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = categories.indexOfFirst {
        it.name == "All"
    }.coerceAtLeast(0)

    inner class CategoryViewHolder(
        private val binding: LayoutParlourCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: SalonCategory, position: Int) {

            binding.imgCategory.setImageResource(category.imageRes)
            binding.tvCategoryName.text = category.name

            val isSelected = position == selectedPosition

            if (isSelected) {
                binding.categoryCard.strokeColor =
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.primaryColor
                    )

                binding.categoryCard.strokeWidth =
                    dpToPx(2)
            } else {
                binding.categoryCard.strokeColor =
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.category_stroke
                    )

                binding.categoryCard.strokeWidth =
                    dpToPx(1)
            }

            binding.categoryCard.setOnClickListener {

                if (selectedPosition != position) {
                    val oldPosition = selectedPosition
                    selectedPosition = position

                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)

                    onCategorySelected(category)
                }
            }
        }

        private fun dpToPx(dp: Int): Int {
            return (dp * binding.root.resources.displayMetrics.density).toInt()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val binding = LayoutParlourCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount(): Int = categories.size
}