package com.alenear.user.presentation.booking.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alenear.user.databinding.ItemSlotBinding
import com.alenear.user.presentation.booking.model.SlotUiModel
import com.alenear.user.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar

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
                    if(!NetworkUtils.isInternetAvailable(binding.root.context)){
                        Snackbar.make(binding.root,"Check Internet Connection", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
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