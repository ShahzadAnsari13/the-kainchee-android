package com.thekainchee.user.presentation.location.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.thekainchee.user.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thekainchee.user.databinding.FragmentSaveAddressBinding
import com.thekainchee.user.domain.model.AddressMode
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.presentation.location.state.AddressState
import com.thekainchee.user.presentation.location.state.MapState
import com.thekainchee.user.presentation.location.viewmodel.AddressSharedViewModel
import com.thekainchee.user.presentation.location.viewmodel.MapViewModel
import com.thekainchee.user.presentation.location.viewmodel.SaveUpdateAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SaveAddressFragment : Fragment() {
    private var _binding: FragmentSaveAddressBinding? = null
    private val binding get() = _binding!!

    private val args: SaveAddressFragmentArgs by navArgs()
    private val viewModel: MapViewModel by viewModels()
    private val saveUpdateAddressViewModel: SaveUpdateAddressViewModel by viewModels()
    private val addressSharedViewModel : AddressSharedViewModel by activityViewModels()
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
        when (addressSharedViewModel.mode) {

            AddressMode.EDIT -> {
                // chip select
                if(addressSharedViewModel.selectedAddress?.label=="Home"){
                    binding.chipHome.isChecked = true
                }else if(addressSharedViewModel.selectedAddress?.label=="Work"){
                    binding.chipWork.isChecked = true
                }else{
                    binding.chipHome.isChecked = true
                }
                binding.btnConfirm.text=getString(R.string.update_address)

            }

            AddressMode.ADD -> {
                binding.chipHome.isChecked = true
            }
        }
        if (selectedAddress == null) {
            viewModel.getAddressFromLatLng(lat, lng)
            binding.locationCons.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    binding.locationCons.visibility = View.GONE
                }
        }

        binding.btnChange.setOnClickListener {
            findNavController().popBackStack()
        }



        binding.btnConfirm.setOnClickListener {
            if (!binding.btnConfirm.isEnabled) return@setOnClickListener
            val baseAddress = selectedAddress
            if (baseAddress == null) {
                Toast.makeText(requireContext(), "Address not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val label = when (binding.chipGroup.checkedChipId) {
                R.id.chipHome -> "Home"
                R.id.chipWork -> "Work"
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
                id = null,
                label = label,
                latitude = baseAddress.latitude,
                longitude = baseAddress.longitude,

                country = baseAddress.country,
                state = baseAddress.state,
                district = baseAddress.district,
                city = baseAddress.city,
                pincode = baseAddress.pincode,

                landmark = finalLandmark,
                details = finalDetails,

                isDefault = when (addressSharedViewModel.mode) {
                    AddressMode.ADD -> true
                    AddressMode.EDIT -> addressSharedViewModel.selectedAddress?.isSelected ?: true
                }
            )
            when(addressSharedViewModel.mode){
                 AddressMode.ADD -> {
                     saveUpdateAddressViewModel.saveAddress(userAddress)
                }
                AddressMode.EDIT ->{
                    saveUpdateAddressViewModel.updateAddress(addressSharedViewModel.selectedAddress?.id,userAddress)
                }
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                saveUpdateAddressViewModel.state.collect { state ->
                    when (state) {

                        is AddressState.Idle -> {
                            when(addressSharedViewModel.mode){
                                AddressMode.ADD -> {
                                    binding.btnConfirm.text = getString(R.string.confirm)
                                    binding.btnConfirm.isEnabled = true
                                }
                                AddressMode.EDIT ->{
                                    binding.btnConfirm.text = getString(R.string.update_address)
                                    binding.btnConfirm.isEnabled = true
                                }
                            }

                        }

                        is AddressState.Loading -> {
                            binding.btnConfirm.text = ""
                            binding.btnConfirm.isEnabled = false

                        }

                        is AddressState.CreateAddress -> {
                            when(addressSharedViewModel.mode){
                                AddressMode.ADD -> {
                                    binding.btnConfirm.text = getString(R.string.confirm)
                                    binding.btnConfirm.isEnabled = true
                                }
                                AddressMode.EDIT ->{
                                    binding.btnConfirm.text = getString(R.string.update_address)
                                    binding.btnConfirm.isEnabled = true
                                }
                            }
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack(R.id.locationListFragment, false)
                        }
                        is AddressState.UpdateAddress->{
                            addressSharedViewModel.mode = AddressMode.ADD
                            addressSharedViewModel.selectedAddress = null
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack(R.id.locationListFragment, false)
                        }

                        is AddressState.Error -> {
                            when(addressSharedViewModel.mode){
                                AddressMode.ADD -> {
                                    binding.btnConfirm.text = getString(R.string.confirm)
                                    binding.btnConfirm.isEnabled = true
                                }
                                AddressMode.EDIT ->{
                                    binding.btnConfirm.text = getString(R.string.update_address)
                                    binding.btnConfirm.isEnabled = true
                                }
                            }
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
                            binding.locationCons.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction {
                                    binding.locationCons.visibility = View.GONE
                                }
                        }

                        is MapState.AddressReceived -> {

                            binding.locationCons.visibility = View.VISIBLE
                            binding.locationCons.alpha = 0f
                            binding.locationCons.animate().alpha(1f).setDuration(200).start()
                            val address = state.address

                            selectedAddress = address

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
