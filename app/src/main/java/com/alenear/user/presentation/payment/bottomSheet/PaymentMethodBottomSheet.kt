package com.alenear.user.presentation.payment.bottomSheet

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.razorpay.Checkout
import com.alenear.user.R
import com.alenear.user.databinding.LayoutPaymentBottomSheetBinding
import com.alenear.user.presentation.booking.model.PaymentSummary
import com.alenear.user.presentation.payment.model.PaymentMethod
import com.alenear.user.presentation.payment.model.VerifyPaymentParams
import com.alenear.user.presentation.payment.state.OnlinePaymentEvent
import com.alenear.user.presentation.payment.state.PaymentEvent
import com.alenear.user.presentation.payment.state.WalletBalanceState
import com.alenear.user.presentation.payment.viewModel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.alenear.user.BuildConfig
import com.alenear.user.databinding.DialogErrorBinding
import com.alenear.user.presentation.payment.state.PaymentCallbackEvent
import com.alenear.user.utils.NetworkUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@AndroidEntryPoint
class PaymentMethodBottomSheet : BottomSheetDialogFragment(){
    var onPaymentSuccess: ((String) -> Unit)? = null
    var onPaymentVerification: ((
        String, // bookingId
        String, // orderId
        String, // paymentId
        String  // signature
    ) -> Unit)? = null

    private var _binding: LayoutPaymentBottomSheetBinding? = null
    private var selectedPaymentMethod: PaymentMethod? = null
    private val binding get() = _binding!!
    private lateinit var paymentSummary: PaymentSummary
    private val paymentViewModel: PaymentViewModel by activityViewModels()
    private var walletBalance: Double = 0.0


    private var paymentOrderId: String? = null
    private var paymentId: String? = null
    private var paymentSignature: String? = null


    private var lastBackPressedTime = 0L
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
        setupBackPress()
        paymentSummary = requireArguments().getParcelable(KEY_PAYMENT_SUMMARY)
            ?: throw IllegalStateException("Payment summary missing")
        binding.btnConfirmPayment.isEnabled = false
        binding.btnConfirmPayment.alpha = 0.5f
        updateSelectionUI()
        observeUiStates()
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
            handlePayment()
        }


    }
    private fun observeUiStates() {
        observeWalletBalance()
        observePayment()
        observeOnlinePayment()
        observePaymentCallback()
    }
    private fun observeWalletBalance(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
        }
    }
    private fun observePayment(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentViewModel.paymentEvent.collect {event->
                    when(event){
                        is PaymentEvent.NavigateToSuccess -> {
                            dismiss()
                            onPaymentSuccess?.invoke(paymentSummary.bookingId)
                        }
                        is PaymentEvent.Error -> {
                            val title = when (selectedPaymentMethod) {
                                PaymentMethod.WALLET -> "Wallet Payment Failed"
                                PaymentMethod.CASH -> "Unable to Confirm Booking"
                                else -> "Something Went Wrong"
                            }


                            showErrorDialog(
                                title = title,
                                message = event.msg
                            )
                        }
                    }

                }
            }
        }
    }
    private fun observeOnlinePayment(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentViewModel.onlinePaymentEvent.collect{event->
                    when(event){
                        is  OnlinePaymentEvent.OpenRazorpay -> {
                            openRazorpay(event)
                        }
                        is OnlinePaymentEvent.PaymentError -> {
                            showErrorDialog(
                                title = "Unable to Initiate Payment",
                                message = event.message
                            )
                        }
                        is OnlinePaymentEvent.NavigateToVerification -> {
                            val orderId = paymentOrderId
                            val paymentId = paymentId
                            val signature = paymentSignature

                            if (
                                orderId != null &&
                                paymentId != null &&
                                signature != null
                            ) {
                                onPaymentVerification?.invoke(
                                    paymentSummary.bookingId,
                                    orderId,
                                    paymentId,
                                    signature
                                )
                                dismiss()
                            }
                        }
                        else -> Unit
                    }

                }
            }
        }
    }
    private fun observePaymentCallback(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentViewModel.paymentCallbackEvent.collect{event->
                    when(event){
                        is PaymentCallbackEvent.Success -> {
                            paymentOrderId = event.orderId
                            paymentId = event.paymentId
                            paymentSignature = event.signature
                            paymentViewModel.navigateToVerification()
                        }
                        is PaymentCallbackEvent.Error -> {
                            showErrorDialog(
                                title = "Payment Unsuccessful",
                                message = "Your payment couldn't be completed. Please try again or choose another payment method."
                            )
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
    private fun handlePayment(){
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            Snackbar.make(binding.root,"No Internet Connection",Snackbar.LENGTH_SHORT).show()
        }else{
            when(selectedPaymentMethod) {

                PaymentMethod.WALLET -> {
                    val bookingAmount = paymentSummary.amount.toDouble()
                    if(walletBalance<bookingAmount){
                        Toast.makeText(
                            requireContext(),
                            "Insufficient balance. Please choose another payment method.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@handlePayment
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
                }
            }
        }
    }
    private fun openRazorpay(event: OnlinePaymentEvent.OpenRazorpay){
        val checkout = Checkout()
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)
        val options = JSONObject().apply {

            put("name", "AleNear")
            put("description", "Parlour Booking")
            put("order_id", event.orderId)
            put("currency", event.currency)
            put("amount", event.amount)
            //put("image", "YOUR_LOGO_URL")
        }
        checkout.open(requireActivity(),options)
    }
    override fun onStart() {
        super.onStart()
        isCancelable = false
        dialog?.setCanceledOnTouchOutside(false)

        val bottomSheet =
            dialog?.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) ?: return

        val behavior = BottomSheetBehavior.from(bottomSheet)

        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        bottomSheet.layoutParams.height = (screenHeight * 0.94).toInt()
        bottomSheet.requestLayout()

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = true
    }

    private fun showErrorDialog(
        title: String,
        message: String
    ) {
        val dialogBinding = DialogErrorBinding.inflate(layoutInflater)

        dialogBinding.tvErrorTitle.text = title
        dialogBinding.tvErrorMessage.text = message

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnErrorOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )

        dialog.show()

        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )
    }


    private fun setupBackPress() {
        requireDialog().setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.action == KeyEvent.ACTION_UP
            ) {
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastBackPressedTime < 2000L) {
                    lastBackPressedTime = 0L
                    dismiss()
                } else {
                    lastBackPressedTime = currentTime

                    Snackbar.make(
                        binding.root,
                        "Going back may cancel your pending booking. Your selected slot may no longer be available. Press back again to close.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}