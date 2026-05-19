package com.thekainchee.user.presentation.service.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemCategoryBinding
import com.thekainchee.user.presentation.service.model.ServiceCategory

class CategoryAdapter(private val list : List<ServiceCategory>, private val onItemClick : (ServiceCategory, Int) -> Unit) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var loadingPosition = -1
    inner class ViewHolder( val binding : ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ServiceCategory){
            binding.tvCategory.text = item.name
            Glide.with(binding.root.context)
                .load(item.image)
                .placeholder(R.drawable.ic_no_data)
                .error(R.drawable.ic_no_data)
                .into(binding.imgCategory)
            binding.viewOverlay.visibility =
                if(bindingAdapterPosition == loadingPosition)
                    View.VISIBLE
                else
                    View.GONE
            binding.progressStatusCheck.visibility =
                if(bindingAdapterPosition == loadingPosition)
                    View.VISIBLE
                else
                    View.GONE


            binding.root.setOnClickListener {
                val position = bindingAdapterPosition

                if(position != RecyclerView.NO_POSITION){

                    onItemClick(item, position)
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    fun enableLoading(position: Int){

        loadingPosition = position
        notifyItemChanged(position)
    }
    fun disableLoading(){

        val oldPosition = loadingPosition

        loadingPosition = -1

        if(oldPosition != -1){
            notifyItemChanged(oldPosition)
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}