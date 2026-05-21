package com.thekainchee.user.presentation.service.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.databinding.ItemBookingPreviewBinding
import com.thekainchee.user.presentation.service.model.BookingPreviewItemUiModel
import com.thekainchee.user.presentation.service.model.BookingPreviewUiModel

class BookingPreviewAdapter(
    private val onRemoveClick:
        (BookingPreviewItemUiModel) -> Unit
) : ListAdapter<
        BookingPreviewItemUiModel,
        BookingPreviewAdapter.BookingPreviewViewHolder
        >(DiffCallback()) {

    inner class BookingPreviewViewHolder(
        private val binding: ItemBookingPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookingPreviewItemUiModel) {

            binding.tvServiceName.text =
                item.name

            binding.tvDuration.text =
                "${item.duration} mins"

            binding.tvPrice.text =
                "₹${item.price}"

            Glide.with(binding.root.context)
                .load(item.image)
                .into(binding.imgService)

            binding.btnRemove.setOnClickListener {
                onRemoveClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookingPreviewViewHolder {

        val binding =
            ItemBookingPreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return BookingPreviewViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BookingPreviewViewHolder,
        position: Int
    ) {

        holder.bind(getItem(position))
    }

    class DiffCallback :
        DiffUtil.ItemCallback<BookingPreviewItemUiModel>() {

        override fun areItemsTheSame(
            oldItem: BookingPreviewItemUiModel,
            newItem: BookingPreviewItemUiModel
        ): Boolean {

            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: BookingPreviewItemUiModel,
            newItem: BookingPreviewItemUiModel
        ): Boolean {

            return oldItem == newItem
        }
    }
}