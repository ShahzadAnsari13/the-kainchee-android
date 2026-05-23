package com.thekainchee.user.presentation.booking.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemSlotBinding
import com.thekainchee.user.databinding.ItemStaffBinding
import com.thekainchee.user.presentation.booking.adapter.BookingStaffAdapter.Companion.DIFF_CALLBACK
import com.thekainchee.user.presentation.booking.model.SlotUiModel
import com.thekainchee.user.presentation.booking.model.StaffUiModel
import okio.Inflater

class BookingSlotAdapter(private val onItemClick : (SlotUiModel)->Unit ) : ListAdapter<SlotUiModel, BookingSlotAdapter.BookingSlotHolder>(DIFF_CALLBACK){
    private var selectedPosition = 0
    companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SlotUiModel>() {

        override fun areItemsTheSame(
            oldItem: SlotUiModel,
            newItem: SlotUiModel
        ): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(
            oldItem: SlotUiModel,
            newItem: SlotUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
    inner class BookingSlotHolder(
        private val binding: ItemSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SlotUiModel, position: Int) {
            binding.apply {

                tvSlotTime.text = item.time
                root.isSelected = position == selectedPosition
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
    ): BookingSlotAdapter.BookingSlotHolder {
        val binding = ItemSlotBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookingSlotHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BookingSlotAdapter.BookingSlotHolder,
        position: Int
    ) {
        holder.bind(getItem(position),position)
    }
}