package com.thekainchee.user.presentation.location.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.thekainchee.user.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thekainchee.user.databinding.FragmentSaveAddressBinding
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.presentation.location.AddressState
import com.thekainchee.user.presentation.location.MapState
import com.thekainchee.user.presentation.location.viewmodel.MapViewModel
import com.thekainchee.user.presentation.location.viewmodel.SaveAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SaveAddressFragment : Fragment() {
    private var _binding: FragmentSaveAddressBinding? = null
    private val binding get() = _binding!!

    private val args: SaveAddressFragmentArgs by navArgs()
    private val viewModel: MapViewModel by viewModels()
    private val saveAddressViewModel: SaveAddressViewModel by viewModels()
    private var selectedAddress: UserAddress? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSaveAddressBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lat = args.latitude.toDouble()
        val lng = args.longitude.toDouble()
        if (viewModel.state.value is MapState.Idle) {
            viewModel.getAddressFromLatLng(lat, lng)
        }

        binding.btnChange.setOnClickListener {
            val action = SaveAddressFragmentDirections
                .actionSaveAddressFragmentToMapFragment(null)

            findNavController().navigate(action)
        }



        binding.btnConfirm.setOnClickListener {
            val baseAddress = selectedAddress
            if (baseAddress == null) {
                Toast.makeText(requireContext(), "Address not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val label = when (binding.chipGroup.checkedChipId) {
                R.id.chipHome -> "Home"
                R.id.chipOffice -> "Work"
                R.id.chipOther -> "Other"
                else -> "Home" // fallback (safe)
            }
            val flat = binding.etFlat.text.toString().trim()
            val street = binding.etStreet.text.toString().trim()
            val landmark = binding.etLandmark.text.toString().trim()
            val details = listOf(flat, street)
                .filter { it.isNotBlank() }
                .joinToString(", ")
            val finalDetails = details.ifBlank {
                baseAddress.details
            }
            val finalLandmark = landmark.ifBlank {
                baseAddress.landmark
            }
            val userAddress = UserAddress(
                label = label, // 👉 abhi static (baad me chip se lenge)
                latitude = baseAddress.latitude,
                longitude = baseAddress.longitude,

                country = baseAddress.country,
                state = baseAddress.state,
                district = baseAddress.district,
                city = baseAddress.city,
                pincode = baseAddress.pincode,

                landmark = finalLandmark,
                details = finalDetails,

                isDefault = true
            )
           saveAddressViewModel.saveAddress(userAddress)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                saveAddressViewModel.state.collect { state ->
                    when (state) {

                        is AddressState.Idle -> {
                             binding.btnConfirm.isEnabled = true
                        }

                        is AddressState.Loading -> {
                            binding.btnConfirm.text = "Saving..."
                            binding.btnConfirm.isEnabled = false

                        }

                        is AddressState.Success -> {
                            binding.btnConfirm.text = "Confirm address"
                            binding.btnConfirm.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }

                        is AddressState.Error -> {
                            binding.btnConfirm.text = "Confirm address"
                            binding.btnConfirm.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when (state) {

                        is MapState.Loading -> {
                            // optional loader
                        }

                        is MapState.AddressReceived -> {
                            val address = state.address

                            selectedAddress = address // 🔥 IMPORTANT

                            binding.tvhead.text =
                                if (address.details.isNullOrBlank()) {
                                    address.city ?: ""
                                } else {
                                    address.details ?: ""
                                }

                            val fullAddress = listOf(
                                address.details,
                                address.city,
                                address.state,
                                address.pincode
                            )
                                .map { it?.trim() }
                                .filter { !it.isNullOrBlank() }
                                .distinct()
                                .joinToString(", ")

                            binding.tvfullAdd.text = fullAddress

                            binding.etLandmark.setText(address.landmark ?: "")
                        }

                        is MapState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
