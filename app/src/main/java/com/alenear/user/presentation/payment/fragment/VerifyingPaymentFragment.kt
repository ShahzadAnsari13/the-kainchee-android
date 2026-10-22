package com.alenear.user.presentation.payment.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alenear.user.R
import com.alenear.user.databinding.FragmentBookingSlotBinding
import com.alenear.user.databinding.FragmentVerifyingPaymentBinding
import com.alenear.user.presentation.booking.fragment.BookingSlotFragmentArgs
import com.alenear.user.presentation.booking.fragment.BookingSlotFragmentDirections
import com.alenear.user.presentation.common.extensions.hide
import com.alenear.user.presentation.common.extensions.show
import com.alenear.user.presentation.common.state.StateViewData
import com.alenear.user.presentation.payment.model.VerifyPaymentParams
import com.alenear.user.presentation.payment.state.OnlinePaymentEvent
import com.alenear.user.presentation.payment.viewModel.PaymentViewModel
import com.alenear.user.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.getValue
class VerifyingPaymentFragment : Fragment() {
    private val navArgs : VerifyingPaymentFragmentArgs by navArgs()
    private val paymentViewModel: PaymentViewModel by activityViewModels()
    private var _binding : FragmentVerifyingPaymentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerifyingPaymentBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookingId = navArgs.bookingId
        binding.tvBookingId.text = bookingId
        val orderId = navArgs.orderId
        val paymentId = navArgs.paymentId
        val signature = navArgs.signature

        val params = VerifyPaymentParams(
            orderId = orderId,
            paymentId = paymentId,
            signature = signature
        )

        withInternet {
            paymentViewModel.verifyPayment(params)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Snackbar.make(
                        binding.root,
                        "Payment verification is in progress. Please wait.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentViewModel.onlinePaymentEvent.collect { event ->
                    when (event) {
                        is OnlinePaymentEvent.NavigateToSuccess -> {
                            findNavController().navigate(
                                VerifyingPaymentFragmentDirections
                                    .actionPaymentVerificationFragmentToBookingSuccessFragment(
                                        navArgs.bookingId
                                    )
                            )
                        }
                        is OnlinePaymentEvent.PaymentError -> {
                            showPaymentError {
                                withInternet {
                                    binding.stateView.hide()
                                    paymentViewModel.verifyPayment(params)
                                }
                            }
                        }

                        is OnlinePaymentEvent.PaymentFailed -> {
                            findNavController().navigate(
                                VerifyingPaymentFragmentDirections
                                    .actionPaymentVerificationFragmentToPaymentFailureFragment(
                                        navArgs.bookingId
                                    )
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
    private fun showPaymentError(
        onRetry: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.error_state_illustration,
                title = "Unable to Verify Payment",
                subtitle = "We couldn't verify your payment right now. Please try again.",
                primaryButtonText = "Retry",
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




}