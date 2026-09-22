package com.thekainchee.user.presentation.auth.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentReferralCodeBottomSheetBinding
import com.thekainchee.user.presentation.auth.state.ReferralState
import com.thekainchee.user.presentation.auth.viewModel.AuthViewModel
import com.thekainchee.user.utils.NetworkUtils
import kotlinx.coroutines.launch


class ReferralCodeBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentReferralCodeBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var phoneNumber: String = ""

        private val viewModel : AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReferralCodeBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneNumber = arguments?.getString(ARG_PHONE_NUMBER).orEmpty()
    }
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observer()
    }

    private fun observer(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.referralState.collect { state ->

                    when (state) {
                        ReferralState.Idle -> Unit

                        ReferralState.Loading -> {
                            setLoading(true)
                            binding.referralInputContainer.visibility = View.VISIBLE
                            binding.referralSuccessContainer.visibility = View.GONE
                        }

                        is ReferralState.Success -> {
                            setLoading(false)
                            binding.referralInputContainer.visibility = View.GONE
                            binding.referralSuccessContainer.visibility = View.VISIBLE
                        }

                        is ReferralState.Error -> {
                            setLoading(false)
                            binding.referralInputContainer.visibility = View.VISIBLE
                            binding.referralSuccessContainer.visibility = View.GONE

                            binding.tilReferralCode.error = state.message
                        }
                    }
                }
            }
        }
    }
    private fun setupClickListeners() {

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                showSnackbar("No Internet Connection")
            }else{
                applyReferralCode()
            }
        }

        binding.btnContinue.setOnClickListener {
            dismiss()
        }
        binding.tvContinueWithoutCode.setOnClickListener {
            dismiss()
        }
    }
    private fun applyReferralCode() {

        val referralCode = binding.etReferralCode
            .text
            ?.toString()
            ?.trim()
            ?.uppercase()
            .orEmpty()

        // Clear previous error
        binding.tilReferralCode.error = null

        if (referralCode.isEmpty()) {
            binding.tilReferralCode.error = "Enter referral code"
            return
        }

        if (referralCode.length != 12) {
            binding.tilReferralCode.error = "Enter a valid referral code"
            return
        }
        viewModel.applyReferral(referralCode, phoneNumber)
        // API call will come here
    }
    private fun setLoading(isLoading: Boolean) {

        binding.btnApply.isEnabled = !isLoading
        binding.etReferralCode.isEnabled = !isLoading
        binding.ivClose.isEnabled = !isLoading

        binding.btnApply.text = if (isLoading) {
            "Applying..."
        } else {
            "Apply"
        }
    }
    companion object {

        private const val ARG_PHONE_NUMBER = "phone_number"

        fun newInstance(phoneNumber: String): ReferralCodeBottomSheet {
            return ReferralCodeBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_PHONE_NUMBER, phoneNumber)
                }
            }
        }
    }
    private fun showSnackbar(message: String) {

         Snackbar.make( binding.root, message, Snackbar.LENGTH_SHORT).show()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}