package com.thekainchee.user.presentation.location.fragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.data.local.datastore.UserPreferencesManager
import com.thekainchee.user.data.mapper.toUI
import com.thekainchee.user.databinding.FragmentLocationListBinding
import com.thekainchee.user.domain.model.AddressMode
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.presentation.location.state.AddressListState
import com.thekainchee.user.presentation.location.adapter.AddressAdapter
import com.thekainchee.user.presentation.location.model.AddressUI
import com.thekainchee.user.presentation.location.state.AddressDeleteEvent
import com.thekainchee.user.presentation.location.viewmodel.AddressSharedViewModel
import com.thekainchee.user.presentation.location.viewmodel.AddressViewModel
import com.thekainchee.user.presentation.location.viewmodel.SearchLocationViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class LocationListFragment : Fragment() {
    private lateinit var adapter: AddressAdapter
    private var _binding: FragmentLocationListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchLocationViewModel by viewModels()
    private val addressViewModel : AddressViewModel by viewModels()

    private val addressSharedViewModel : AddressSharedViewModel by activityViewModels()
    private var fullList: List<UserAddress> = emptyList()
    @Inject
    lateinit var preferencesManager : UserPreferencesManager
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

        if (!NetworkUtils.isInternetAvailable(requireContext())){
            binding.layoutNoInternet.visibility = View.VISIBLE
            binding.mainContent.visibility = View.GONE
        }else{
            setupAdapter()
            setUpRecyclerView()
        }

        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                binding.layoutNoInternet.visibility = View.GONE
                binding.mainContent.visibility = View.VISIBLE
                if(!::adapter.isInitialized){
                   setupAdapter()
                    setUpRecyclerView()
                }
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    addressViewModel.actionId.collect { id ->
                        if(::adapter.isInitialized){
                            adapter.actionId = id
                            adapter.notifyDataSetChanged()
                        }

                    }
                }



                launch {
                    addressViewModel.event.collect { event ->
                        when (event) {
                            is AddressDeleteEvent.ShowMessage -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                launch {
                    addressViewModel.state.collect { state ->
                        when (state) {
                            is AddressListState.Loading -> { /* loader */ }

                            is AddressListState.Success -> {
                                fullList = state.data
                                val uiList = fullList.map { it.toUI() }

                                if(::adapter.isInitialized){
                                    adapter.submitList(uiList)
                                }
                            }

                            is AddressListState.Error -> { /* error */ }
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.searchResults.collect {list ->

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

                        if (::adapter.isInitialized) {
                            adapter.submitList(uiList)
                        }
                    }
                }
            }
        }




        binding.cardCurrentLocation.setOnClickListener {
            val action = LocationListFragmentDirections
                .actionLocationListFragmentToMapFragment(null)

            findNavController().navigate(action)
        }
        binding.tvAddNew.setOnClickListener {
            val action = LocationListFragmentDirections
                .actionLocationListFragmentToMapFragment(null)

            findNavController().navigate(action)
        }


        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString()

            if (query.length >= 3) {
                viewModel.searchLocation(query)
                Log.d("SEARCH", query)
            }else {
                binding.tvSaved.text = "Saved addresses"
                binding.tvAddNew.visibility = View.VISIBLE

                if (::adapter.isInitialized) {
                    val uiList = fullList.map { it.toUI() }
                    adapter.submitList(uiList)
                }
            }
        }



    }
    private fun setupAdapter(){
        adapter = AddressAdapter(
            onItemClick = {item->
                if(!NetworkUtils.isInternetAvailable(requireContext())){
                    binding.layoutNoInternet.visibility = View.VISIBLE
                    binding.mainContent.visibility = View.GONE
                }else{
                    if (item.placeId != null) {
                        val action = LocationListFragmentDirections
                            .actionLocationListFragmentToMapFragment(placeId = item.placeId)
                        findNavController().navigate(action)
                    }
                    else {
                        //store id in dataStore
                        item.id?.let {
                            lifecycleScope.launch {
                                preferencesManager.saveSelectedAddressId(it)
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            "Location selected successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

            },
            onMenuClick = {item,view->
                val bottomSheet = AddressOptionsBottomSheet(
                    onEditClick = {
                        if(!NetworkUtils.isInternetAvailable(requireContext())){
                            binding.layoutNoInternet.visibility = View.VISIBLE
                            binding.mainContent.visibility = View.GONE
                        }else {
                            addressSharedViewModel.mode = AddressMode.EDIT
                            addressSharedViewModel.selectedAddress = item
                            val action = LocationListFragmentDirections
                                .actionLocationListFragmentToMapFragment(null)

                            findNavController().navigate(action)
                        }

                    },
                    onDeleteClick = deleteClick@{
                        if(!NetworkUtils.isInternetAvailable(requireContext())){
                            binding.layoutNoInternet.visibility = View.VISIBLE
                            binding.mainContent.visibility = View.GONE
                        }else {
                            if (addressViewModel.actionId.value != null) return@deleteClick

                            val id = item.id ?: return@deleteClick

                            if (item.isSelected) {
                                Toast.makeText(requireContext(), "Cannot delete default address", Toast.LENGTH_SHORT).show()
                                return@deleteClick
                            }

                            addressViewModel.deleteAddress(id)
                        }


                    },
                    onSetDefaultClick = setDefaultClick@{
                        if(!NetworkUtils.isInternetAvailable(requireContext())){
                            binding.layoutNoInternet.visibility = View.VISIBLE
                            binding.mainContent.visibility = View.GONE
                        }else {
                            if (addressViewModel.actionId.value != null) return@setDefaultClick
                            item.id?.let{
                                addressViewModel.setDefaultAddress(it)
                            }
                        }

                    }
                )

                bottomSheet.show(parentFragmentManager, "AddressOptionsBottomSheet")

            }


        )
    }

    private fun setUpRecyclerView(){
        binding.rvLocations.apply {
            adapter = this@LocationListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            binding.etSearch.setText("")
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}