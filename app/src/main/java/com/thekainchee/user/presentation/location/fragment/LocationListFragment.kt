package com.thekainchee.user.presentation.location.fragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentLocationListBinding
import com.thekainchee.user.presentation.location.adapter.AddressAdapter
import com.thekainchee.user.presentation.location.model.AddressUI
import com.thekainchee.user.presentation.location.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationListFragment : Fragment() {
    private lateinit var adapter: AddressAdapter
    private var _binding: FragmentLocationListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
         _binding = FragmentLocationListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AddressAdapter(
            onItemClick = {item->
                if (item.placeId != null) {
                    findNavController().navigate(
                        R.id.action_locationListFragment_to_mapFragment,
                        bundleOf("placeId" to item.placeId)
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_locationListFragment_to_mapFragment
                    )
                }
            },
            onMenuClick = {item,view->

            }
        )

        binding.rvLocations.apply {
            adapter = this@LocationListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString()

            if (query.length >= 3) {
                viewModel.searchLocation(query)
                Log.d("SEARCH", query)
            }  else {
                // TODO: original list show (saved addresses)
            }
        }
        viewModel.searchResults.observe(viewLifecycleOwner) { list ->

            val uiList = list.mapIndexed { index, item ->
                AddressUI(
                    id = index.toString(),
                    label = item.primaryText,
                    address = item.secondaryText,
                    latitude = 0.0,
                    longitude = 0.0,
                    placeId = item.placeId,
                    isFromSearch = true
                )
            }
            binding.tvSaved.text = "Search results"
            binding.tvSaved.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.gray)
                    )
            binding.tvAddNew.visibility = View.GONE

            adapter.submitList(uiList)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}