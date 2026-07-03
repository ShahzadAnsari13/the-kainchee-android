package com.thekainchee.user.presentation.profile.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentEditProfileBottomSheetBinding
import com.thekainchee.user.presentation.profile.state.EditProfileEvent
import com.thekainchee.user.presentation.profile.viewModel.ProfileViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class EditProfileBottomSheet : BottomSheetDialogFragment() {
    private var name: String = ""
    private var countryCode: String = ""
    private var phoneNumber: String = ""
    private var _binding: FragmentEditProfileBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel : ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("name").orEmpty()
            countryCode = it.getString("country_code").orEmpty()
            phoneNumber = it.getString("phone_number").orEmpty()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etName.setText(name)
        binding.etCountryCode.setText(countryCode)
        binding.etPhone.setText(phoneNumber)
        binding.btnSave.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(requireView(), "No Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                val name = binding.etName.text.toString()
                profileViewModel.updateProfile(name)
                binding.btnSave.isEnabled = false
                binding.progressSave.visibility = View.VISIBLE
                binding.btnSave.text = ""
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                profileViewModel.event.collect{event ->
                    when(event){
                        is EditProfileEvent.Success -> {
                            binding.btnSave.isEnabled = true
                            binding.progressSave.visibility = View.GONE
                            binding.btnSave.text = "Save Changes"
                            Snackbar.make(requireView(), "Profile Updated", Snackbar
                                .LENGTH_SHORT).show()
                            dismiss()
                        }
                        is EditProfileEvent.Error -> {
                            binding.btnSave.isEnabled = true
                            binding.progressSave.visibility = View.GONE
                            binding.btnSave.text = "Save Changes"
                            Snackbar.make(requireView(), event.message, Snackbar
                                .LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    companion object {

        fun newInstance(
            name: String,
            countryCode: String,
            phoneNumber: String
        ): EditProfileBottomSheet {

            return EditProfileBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                    putString("country_code", countryCode)
                    putString("phone_number", phoneNumber)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}