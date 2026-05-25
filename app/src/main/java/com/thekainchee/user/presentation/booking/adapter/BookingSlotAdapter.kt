package com.thekainchee.user.presentation.booking.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemSlotBinding
import com.thekainchee.user.presentation.booking.model.SlotUiModel
import okio.Inflater

class BookingSlotAdapter(private val onItemClick : (SlotUiModel)->Unit ) : RecyclerView.Adapter<BookingSlotAdapter.BookingSlotHolder>(){
    private var selectedPosition = -1
    private val items = mutableListOf<SlotUiModel>()
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
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitSlots(newList: List<SlotUiModel>) {

        selectedPosition = -1

        items.clear()
        items.addAll(newList)

        notifyDataSetChanged()
    }
}