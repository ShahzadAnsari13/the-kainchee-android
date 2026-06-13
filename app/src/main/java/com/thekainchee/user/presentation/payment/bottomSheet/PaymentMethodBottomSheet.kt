package com.thekainchee.user.presentation.payment.bottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.LayoutPaymentBottomSheetBinding
import com.thekainchee.user.presentation.booking.model.PaymentSummary
import com.thekainchee.user.presentation.payment.model.PaymentMethod
import com.thekainchee.user.presentation.payment.state.WalletBalanceState
import com.thekainchee.user.presentation.payment.viewModel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class PaymentMethodBottomSheet : BottomSheetDialogFragment() {
    private var _binding: LayoutPaymentBottomSheetBinding? = null
    private var selectedPaymentMethod: PaymentMethod? = null
    private val binding get() = _binding!!
    private lateinit var paymentSummary: PaymentSummary
    private val paymentViewModel: PaymentViewModel by viewModels()
    companion object {

        private const val KEY_PAYMENT_SUMMARY = "payment_summary"

        fun newInstance(
            paymentSummary: PaymentSummary
        ): PaymentMethodBottomSheet {

            return PaymentMethodBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(
                        KEY_PAYMENT_SUMMARY,
                        paymentSummary
                    )
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutPaymentBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentSummary = requireArguments().getParcelable(KEY_PAYMENT_SUMMARY)
            ?: throw IllegalStateException("Payment summary missing")
        binding.btnConfirmPayment.isEnabled = false
        binding.btnConfirmPayment.alpha = 0.5f
        updateSelectionUI()
        observeWalletBalance()
        paymentViewModel.getWalletBalance()
        binding.tvStaffName.text = paymentSummary.staffName
        binding.tvDateTime.text = paymentSummary.dateTime
        binding.tvAmount.text = "₹${paymentSummary.amount}"
        binding.cardWallet.setOnClickListener {
            selectedPaymentMethod = PaymentMethod.WALLET
            updateSelectionUI()
        }

        binding.cardOnline.setOnClickListener {
            selectedPaymentMethod = PaymentMethod.ONLINE
            updateSelectionUI()
        }

        binding.cardCash.setOnClickListener {
            selectedPaymentMethod = PaymentMethod.CASH
            updateSelectionUI()
        }
        binding.btnConfirmPayment.setOnClickListener {
            when(selectedPaymentMethod) {

                PaymentMethod.WALLET -> {
                    Log.d("PAYMENT", "Wallet Selected")
                }

                PaymentMethod.ONLINE -> {
                    Log.d("PAYMENT", "Online Selected")
                }

                PaymentMethod.CASH -> {
                    Log.d("PAYMENT", "Cash Selected")
                }

                null -> {
                    Log.d("PAYMENT", "No Method Selected")
                }
            }
        }


    }

    private fun observeWalletBalance() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    paymentViewModel.walletBalanceState.collect { state ->
                        when (state) {
                            is WalletBalanceState.Idle -> {

                            }

                            is WalletBalanceState.Loading -> {
                                binding.tvWalletBalance.text = "Loading..."
                            }

                            is WalletBalanceState.Success -> {
                                binding.tvWalletBalance.text = "Available Balance ₹${state.balance}"
                            }

                            is WalletBalanceState.Error -> {
                                binding.tvWalletBalance.text = "Unable to load balance"
                            }
                        }
                    }
                }

            }
        }
    }
    private fun updateSelectionUI(){
        val gray = requireContext().getColor(R.color.light_gray)
        val primary = requireContext().getColor(R.color.primaryColor)
        binding.cardWallet.strokeColor = gray
        binding.cardOnline.strokeColor = gray
        binding.cardCash.strokeColor = gray

        when(selectedPaymentMethod){
            PaymentMethod.WALLET -> {
                binding.cardWallet.strokeColor = primary
            }
            PaymentMethod.ONLINE -> {
                binding.cardOnline.strokeColor = primary
            }
            PaymentMethod.CASH -> {
                binding.cardCash.strokeColor = primary
            }
            null -> Unit
        }
        binding.btnConfirmPayment.isEnabled =
            selectedPaymentMethod != null

        binding.btnConfirmPayment.alpha =
            if (selectedPaymentMethod != null) 1f else 0.5f


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}