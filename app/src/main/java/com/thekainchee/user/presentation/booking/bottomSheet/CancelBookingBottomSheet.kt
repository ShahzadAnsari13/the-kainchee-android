package com.thekainchee.user.presentation.booking.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentCancelBookingBottomSheetBinding
import com.thekainchee.user.presentation.booking.event.CancelBookingEvent
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.utils.NetworkUtils
import kotlinx.coroutines.launch


class CancelBookingBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentCancelBookingBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var selectedReason: String? = null
    private var isCancelling = false
    private val viewModel: BookingViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private lateinit var bookingId: String
    private val reasonMap = mapOf(
        R.id.rbPlansChanged to "Plans changed",
        R.id.rbAnotherParlour to "Found another parlour/service",
        R.id.rbPriceHigh to "Price is too high",
        R.id.rbTooFar to "Parlour is too far",
        R.id.rbParlourRequested to "Parlour requested me to cancel",
        R.id.rbParlourUnavailable to "Parlour is closed / unavailable",
        R.id.rbWrongBooking to "Wrong booking / booked by mistake",
        R.id.rbTimeDoesntWork to "Appointment time doesn’t work",
        R.id.rbServiceUnavailable to "Service/stylist not available",
        R.id.rbBetterOption to "Found a better option",
        R.id.rbTechnicalIssue to "Technical / booking issue",
        R.id.rbOther to "Other"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCancelBookingBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingId = requireArguments().getString(ARG_BOOKING_ID)
            ?: throw IllegalArgumentException("Booking ID is required")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupReasonSelection()
        setupActions()
        observeCancellation()
    }
    private fun observeCancellation() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.cancelBookingEvent.collect { event ->

                    when (event) {

                        is CancelBookingEvent.Success -> {
                            Toast.makeText(
                                requireContext(),
                                event.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            dismiss()
                        }

                        is CancelBookingEvent.Error -> {

                            hideCancellationLoading()

                            Toast.makeText(
                                requireContext(),
                                event.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun setupReasonSelection() {

        binding.radioGroupReasons.setOnCheckedChangeListener { _, checkedId ->

            selectedReason = reasonMap[checkedId]

            if (checkedId == R.id.rbOther) {

                binding.tilOtherReason.visibility = View.VISIBLE

            } else {

                binding.tilOtherReason.visibility = View.GONE

                binding.etOtherReason.text?.clear()
                binding.tilOtherReason.error = null
            }
        }
    }
    private fun setupActions() {

        binding.btnKeepBooking.setOnClickListener {
            dismiss()
        }

        binding.btnConfirmCancellation.setOnClickListener {
            if (isCancelling) return@setOnClickListener
            if (!validateReason()) {
                return@setOnClickListener
            }

            val finalReason = if (selectedReason == "Other") {
                binding.etOtherReason.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()
            } else {
                selectedReason.orEmpty()
            }
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            isCancelling = true

            // Loading show
            showCancellationLoading()

            // API call

            viewModel.cancelBooking(
                bookingId = bookingId,
                reason = finalReason
            )
        }
    }
    private fun validateReason(): Boolean {

        if (selectedReason == null) {

            Toast.makeText(
                requireContext(),
                "Please select a cancellation reason",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }

        if (selectedReason == "Other") {

            val otherReason = binding.etOtherReason.text
                ?.toString()
                ?.trim()
                .orEmpty()

            if (otherReason.isEmpty()) {

                binding.tilOtherReason.error =
                    "Please enter your reason"

                return false
            }

            binding.tilOtherReason.error = null
        }

        return true
    }

    private fun showCancellationLoading() {

        binding.btnConfirmCancellation.text = ""

        binding.btnConfirmCancellation.isEnabled = false
        binding.btnKeepBooking.isEnabled = false

        binding.progressCancellation.visibility = View.VISIBLE
    }

    private fun hideCancellationLoading() {

        isCancelling = false

        binding.progressCancellation.visibility = View.GONE

        binding.btnConfirmCancellation.text = "Confirm Cancellation"

        binding.btnConfirmCancellation.isEnabled = true
        binding.btnKeepBooking.isEnabled = true
    }
    companion object {

        private const val ARG_BOOKING_ID = "booking_id"

        fun newInstance(bookingId: String): CancelBookingBottomSheet {

            return CancelBookingBottomSheet().apply {

                arguments = Bundle().apply {
                    putString(ARG_BOOKING_ID, bookingId)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}