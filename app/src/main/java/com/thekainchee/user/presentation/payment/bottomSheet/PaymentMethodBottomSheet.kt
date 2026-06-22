package com.thekainchee.user.presentation.payment.bottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.thekainchee.user.R
import com.thekainchee.user.databinding.LayoutPaymentBottomSheetBinding
import com.thekainchee.user.presentation.booking.model.PaymentSummary
import com.thekainchee.user.presentation.payment.model.PaymentMethod
import com.thekainchee.user.presentation.payment.model.VerifyPaymentParams
import com.thekainchee.user.presentation.payment.state.OnlinePaymentEvent
import com.thekainchee.user.presentation.payment.state.PaymentEvent
import com.thekainchee.user.presentation.payment.state.WalletBalanceState
import com.thekainchee.user.presentation.payment.viewModel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.thekainchee.user.BuildConfig
import com.thekainchee.user.presentation.payment.state.PaymentCallbackEvent

@AndroidEntryPoint
class PaymentMethodBottomSheet : BottomSheetDialogFragment(){
    var onPaymentSuccess: ((String) -> Unit)? = null
    private var _binding: LayoutPaymentBottomSheetBinding? = null
    private var selectedPaymentMethod: PaymentMethod? = null
    private val binding get() = _binding!!
    private lateinit var paymentSummary: PaymentSummary
    private val paymentViewModel: PaymentViewModel by activityViewModels()
    private var walletBalance: Double = 0.0
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
                    val bookingAmount = paymentSummary.amount.toDouble()
                    if(walletBalance<bookingAmount){
                        Toast.makeText(
                            requireContext(),
                            "Insufficient balance. Please choose another payment method.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }

                    paymentViewModel.payWithWallet(bookingId = paymentSummary.bookingId)
                }

                PaymentMethod.ONLINE -> {
                    paymentViewModel.createOrder(bookingId = paymentSummary.bookingId)
                }

                PaymentMethod.CASH -> {
                   paymentViewModel.payWithCash(bookingId = paymentSummary.bookingId)
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
                                walletBalance = state.balance
                                val bookingAmount = paymentSummary.amount.toDouble()
                                if (walletBalance < bookingAmount) {

                                    binding.tvWalletBalance.text =
                                        "Available Balance ₹$walletBalance • Insufficient Balance"

                                } else {

                                    binding.tvWalletBalance.text =
                                        "Available Balance ₹$walletBalance"
                                }
                            }

                            is WalletBalanceState.Error -> {
                                binding.tvWalletBalance.text = "Unable to load balance"
                            }
                        }
                    }
                }
                launch {
                    paymentViewModel.paymentEvent.collect {event->
                        when(event){
                            is PaymentEvent.NavigateToSuccess -> {
                                Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_SHORT).show()
                                dismiss()
                                onPaymentSuccess?.invoke(paymentSummary.bookingId)
                            }
                            is PaymentEvent.Message -> {
                                Toast.makeText(requireContext(), event.msg, Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
                launch {
                    paymentViewModel.onlinePaymentEvent.collect{event->
                        when(event){
                            is  OnlinePaymentEvent.OpenRazorpay -> {
                                Toast.makeText(requireContext(), "Open razorpay", Toast.LENGTH_SHORT).show()
                                openRazorpay(event)
                            }
                            is OnlinePaymentEvent.Message -> {
                                Log.d("RAZORPAY", event.message)

                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                            }
                            is OnlinePaymentEvent.NavigateToSuccess -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Payment Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onPaymentSuccess?.invoke(paymentSummary.bookingId)

                                dismiss()
                            }
                        }

                            }
                }
                launch{
                    paymentViewModel.paymentCallbackEvent.collect{event->
                        when(event){
                            is PaymentCallbackEvent.Success -> {
                                Log.d("PAYMENT", "Payment Success")
                                val params = VerifyPaymentParams(
                                    orderId = event.orderId,
                                    paymentId = event.paymentId,
                                    signature = event.signature
                                )

                                paymentViewModel.verifyPayment(params)
                            }
                            is PaymentCallbackEvent.Error -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
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
    private fun openRazorpay(event: OnlinePaymentEvent.OpenRazorpay){
        val checkout = Checkout()
        Log.d("RAZORPAY", event.orderId)
        Log.d("RAZORPAY", BuildConfig.RAZORPAY_KEY_ID)
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)
        val options = JSONObject().apply {

            put("name", "THE KAINCHEE")
            put("description", "Salon Booking")
            put("order_id", event.orderId)
            put("currency", event.currency)
            put("amount", event.amount)
        }
        checkout.open(requireActivity(),options)


    }
}