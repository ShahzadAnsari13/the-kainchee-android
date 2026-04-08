package com.thekainchee.user.presentation.location.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemLocationBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.transition.Visibility
import com.thekainchee.user.R
import com.thekainchee.user.presentation.location.model.AddressUI


class AddressAdapter(
    private val onItemClick: (AddressUI) -> Unit,
    private val onMenuClick: (AddressUI, View) -> Unit
) : ListAdapter<AddressUI, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    var actionId: String? = null
    inner class AddressViewHolder(val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = getItem(position) ?: return

        val iconRes = when(item.label){
            "Home" -> R.drawable.ic_home
            "Work" -> R.drawable.ic_work
            else -> R.drawable.ic_location2
        }
        holder.binding.ivIcon.setImageResource(iconRes)
        holder.binding.tvLabel.text = item.label
        holder.binding.tvAddress.text = item.address
        holder.binding.tvSelect.visibility =
            if (item.isSelected) View.VISIBLE else View.GONE



        if (item.isFromSearch) {
            holder.binding.ivMenu.visibility = View.GONE
            holder.binding.ivMenu.setOnClickListener(null)
        } else {
            holder.binding.ivMenu.setOnClickListener { view ->
                onMenuClick(item, view)
            }
        }
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        if (item.id == actionId) {
            holder.binding.ivMenu.visibility = View.GONE
            holder.binding.progressBar.visibility = View.VISIBLE
        } else {
            holder.binding.ivMenu.visibility = View.VISIBLE
            holder.binding.progressBar.visibility = View.GONE
        }


    }

}