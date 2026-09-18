package com.thekainchee.user.presentation.booking.bottomSheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentServicesBottomSheetBinding
import com.thekainchee.user.presentation.booking.adapter.BookingServicesAdapter
import com.thekainchee.user.presentation.booking.model.BookingDetailUiModel
import com.thekainchee.user.utils.DateFormatter

class ServicesBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentServicesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var booking: BookingDetailUiModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        booking = requireArguments().getParcelable(ARG_BOOKING)
            ?: throw IllegalArgumentException("Booking data is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentServicesBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupServices()
        setupSummary()
        setupActions()
    }
    private fun setupServices() {

        binding.rvServices.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = BookingServicesAdapter(
                booking.services
            )
            isNestedScrollingEnabled = false
        }
    }
    private fun setupSummary() {

        binding.tvTotalServices.text =
            booking.services.size.toString()

        binding.tvTotalDuration.text =
            DateFormatter.formatDuration(booking.totalDurationMinutes)

        binding.tvTotalAmount.text =
            "₹${booking.totalPrice.toInt()}"
    }

    private fun setupActions() {

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
    companion object {

        private const val ARG_BOOKING = "booking"

        fun newInstance(
            booking: BookingDetailUiModel
        ): ServicesBottomSheet {

            return ServicesBottomSheet().apply {

                arguments = Bundle().apply {
                    putParcelable(ARG_BOOKING, booking)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}