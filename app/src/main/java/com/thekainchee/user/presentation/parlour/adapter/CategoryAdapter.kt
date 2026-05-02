package com.thekainchee.user.presentation.parlour.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.ItemCategoryBinding
import com.thekainchee.user.presentation.parlour.model.ServiceCategory

class CategoryAdapter(private val list : List<ServiceCategory>, private val onItemClick : (ServiceCategory) -> Unit) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder( val binding : ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ServiceCategory){
            binding.tvCategory.text = item.name
            Glide.with(binding.root.context)
                .load(item.image)
                .placeholder(R.drawable.ic_no_data)
                .error(R.drawable.ic_no_data)
                .into(binding.imgCategory)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}