package com.thekainchee.user.presentation.booking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemStaffBinding
import com.thekainchee.user.presentation.booking.model.StaffUiModel

class BookingStaffAdapter(private val onItemClick: (StaffUiModel) -> Unit) : ListAdapter<StaffUiModel, BookingStaffAdapter.BookingStaffViewHolder>(DIFF_CALLBACK){
    private var selectedPosition = 0
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StaffUiModel>() {

            override fun areItemsTheSame(
                oldItem: StaffUiModel,
                newItem: StaffUiModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: StaffUiModel,
                newItem: StaffUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    inner class BookingStaffViewHolder(
        private val binding: ItemStaffBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StaffUiModel, position: Int) {
            binding.apply {

                tvStaffName.text = item.name
                tvStaffInfo.text = "⭐ ${item.rating.average} • ${item.experience} yrs"
                itemView.isSelected = position == selectedPosition
                Glide.with(binding.root)
                    .load(item.image)
                    .placeholder(R.drawable.ic_no_data)
                    .error(R.drawable.ic_no_data)
                    .into(imgStaff)

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = position

                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    onItemClick(item)
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookingStaffAdapter.BookingStaffViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookingStaffViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BookingStaffAdapter.BookingStaffViewHolder,
        position: Int
    ) {
       holder.bind(getItem(position),position)
    }

}