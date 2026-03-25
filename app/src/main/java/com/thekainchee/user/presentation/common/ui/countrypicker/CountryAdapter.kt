package com.thekainchee.user.presentation.common.ui.countrypicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.databinding.ItemCountryBinding
import com.thekainchee.user.presentation.common.model.Country

class CountryAdapter (private val countries:List<Country>,private val onCountryClick:(Country)->Unit) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {
    inner class CountryViewHolder(private val binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(country: Country){
            binding.tvFlag.text = country.flag
            binding.tvCountryName.text = country.name
            binding.tvDialCode.text = country.dialCode
            binding.root.setOnClickListener {
                onCountryClick(country)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryAdapter.CountryViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryAdapter.CountryViewHolder, position: Int) {
        holder.bind(countries[position])
    }

    override fun getItemCount(): Int {
        return countries.size
    }
}