package com.thekainchee.user.presentation.referral.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentWithdrawReferralRewardBottomSheetBinding
import com.thekainchee.user.presentation.booking.bottomSheet.SupportBottomSheet
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.referral.state.WithdrawReferralState
import com.thekainchee.user.presentation.referral.viewModel.ReferralViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class WithdrawReferralRewardBottomSheet :
    BottomSheetDialogFragment() {

    private var _binding: FragmentWithdrawReferralRewardBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var referralReward = 0
    private val viewModel: ReferralViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawReferralRewardBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        referralReward = requireArguments().getInt("referral_reward")

        binding.tvWithdrawAmount.text = "₹$referralReward"
        binding.tvProcessingAmount.text = "₹$referralReward"
        observeWithdrawState()
        setupClickListeners()
    }

    private fun setupClickListeners(){
        binding.btnConfirmWithdraw.setOnClickListener {
            withInternet {
                viewModel.withdrawReferralReward()
            }
        }
        binding.btnDone.setOnClickListener {
            dismiss()
        }
        binding.btnRetryWithdraw.setOnClickListener {
            withInternet {
                viewModel.withdrawReferralReward()
            }
        }
        binding.ivCloseError.setOnClickListener {
            dismiss()
        }
        binding.tvContactSupport.setOnClickListener {

            SupportBottomSheet()
                .show(
                    childFragmentManager,
                    "SupportBottomSheet"
                )
        }
    }
    private fun observeWithdrawState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.withdrawReferralState.collect { state ->

                    when (state) {

                        WithdrawReferralState.Idle -> Unit

                        WithdrawReferralState.Loading -> {
                            binding.layoutProcessing.visibility = View.VISIBLE
                            binding.layoutConfirmation.visibility = View.GONE
                            binding.layoutSuccess.visibility = View.GONE
                            binding.layoutError.visibility = View.GONE
                        }

                        is WithdrawReferralState.Success -> {
                            binding.layoutProcessing.visibility = View.GONE
                            binding.layoutConfirmation.visibility = View.GONE
                            binding.layoutSuccess.visibility = View.VISIBLE
                            binding.layoutError.visibility = View.GONE
                            binding.tvSuccessAmount.text = "₹${state.amount}"
                        }

                        is WithdrawReferralState.Error -> {
                            binding.layoutProcessing.visibility = View.GONE
                            binding.layoutConfirmation.visibility = View.GONE
                            binding.layoutSuccess.visibility = View.GONE
                            binding.layoutError.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
    companion object {
        fun newInstance(referralReward: Int): WithdrawReferralRewardBottomSheet {
            return WithdrawReferralRewardBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt("referral_reward", referralReward)
                }
            }
        }
    }
    private fun showNoInternetState(
        retryText: String = "Retry",
        onRetry: () -> Unit
    ){
        binding.stateView.show(
            StateViewData(
                image = R.drawable.no_internet,
                title = "No Internet Connection",
                subtitle = "Please check your internet connection and try again.",
                primaryButtonText = retryText,
                onPrimaryClick = onRetry
            )
        )
    }
    private fun withInternet(
        onConnected: () -> Unit
    ) {
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            showNoInternetState {
                if (!NetworkUtils.isInternetAvailable(requireContext())) {
                    Snackbar.make(
                        binding.root,
                        "No Internet Connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    binding.stateView.hide()
                    onConnected()
                }
            }
        } else {
            onConnected()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}