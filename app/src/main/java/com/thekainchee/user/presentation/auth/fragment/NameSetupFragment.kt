package com.thekainchee.user.presentation.auth.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.databinding.FragmentNameSetupBinding
import com.thekainchee.user.presentation.dashboard.DashboardActivity
import com.thekainchee.user.presentation.profile.state.EditProfileEvent
import com.thekainchee.user.presentation.profile.viewModel.ProfileViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class NameSetupFragment : Fragment() {
    private var _binding: FragmentNameSetupBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel : ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNameSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(requireView(), "No Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                val name = binding.etName.text.toString().trim()
                if (name.isEmpty()) {
                    binding.etName.error = "Name is required"
                    return@setOnClickListener
                }
                profileViewModel.updateProfile(name)
                binding.btnContinue.isEnabled = false
                binding.progressSave.visibility = View.VISIBLE
                binding.btnContinue.text = ""
            }
        }
        binding.etName.doAfterTextChanged {
            binding.etName.error = null
        }

        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                profileViewModel.event.collect{event ->
                    when(event){
                        is EditProfileEvent.Success -> {
                            binding.btnContinue.isEnabled = true
                            binding.progressSave.visibility = View.GONE
                            binding.btnContinue.text = "Continue"
                            Snackbar.make(
                                requireView(),
                                "Name saved successfully",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    requireContext(),
                                    DashboardActivity::class.java
                                )
                            )

                            requireActivity().finish()
                        }
                        is EditProfileEvent.Error -> {
                            binding.btnContinue.isEnabled = true
                            binding.progressSave.visibility = View.GONE
                            binding.btnContinue.text = "Continue"
                            Snackbar.make(requireView(), event.message, Snackbar
                                .LENGTH_SHORT).show()
                        }
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