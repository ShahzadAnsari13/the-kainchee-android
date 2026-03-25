package com.thekainchee.user.presentation.common.ui.countrypicker

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekainchee.user.databinding.BottomSheetCountryPickerBinding
import com.thekainchee.user.presentation.common.data.CountryProvider
import com.thekainchee.user.presentation.common.model.Country
class CountryPickerBottomSheet(
    private val onCountrySelected: (Country) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCountryPickerBinding? = null
    private val binding get() = _binding!!

    private val countries = CountryProvider.getCountries()

    private lateinit var adapter: CountryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetCountryPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = CountryAdapter(countries) { country ->
            onCountrySelected(country)
            dismiss()
        }

        binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCountries.adapter = adapter

        setupSearch()
    }

    private fun setupSearch() {

        binding.etSearch.addTextChangedListener {editable: Editable? ->

            val query = editable.toString().lowercase()

            val filtered = countries.filter {
                it.name.lowercase().contains(query)
            }

            adapter = CountryAdapter(filtered) { country ->
                onCountrySelected(country)
                dismiss()
            }

            binding.rvCountries.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}