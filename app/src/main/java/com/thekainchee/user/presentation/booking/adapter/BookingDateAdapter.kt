package com.thekainchee.user.presentation.booking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemDateBinding
import com.thekainchee.user.databinding.ItemSlotBinding
import com.thekainchee.user.presentation.booking.model.DateUiModel
import com.thekainchee.user.presentation.booking.model.SlotUiModel
import java.time.LocalDate

class BookingDateAdapter(private val onItemClick : (DateUiModel)->Unit) : ListAdapter<DateUiModel, BookingDateAdapter.BookingDateHolder>(DIFF_CALLBACK){

    private var selectedPosition = 0
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DateUiModel>() {

            override fun areItemsTheSame(
                oldItem: DateUiModel,
                newItem: DateUiModel
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: DateUiModel,
                newItem: DateUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    inner class BookingDateHolder(
        private val binding: ItemDateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DateUiModel, position: Int) {
            binding.apply {
                val isToday = item.fullDate == LocalDate.now().toString()
                val isTomorrow = item.fullDate == LocalDate.now().plusDays(1).toString()
                if(isToday){
                    tvDay.text = "Tod"
                }else if(isTomorrow){
                    tvDay.text = "Tom"
                }else{
                    tvDay.text = item.day
                }
                tvDate.text = item.date
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
    ): BookingDateAdapter.BookingDateHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookingDateHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BookingDateAdapter.BookingDateHolder,
        position: Int
    ) {
        holder.bind(getItem(position),position)
    }


}