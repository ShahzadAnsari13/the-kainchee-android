package com.thekainchee.user.presentation.booking.bottomSheet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentShareBookingBottomSheetBinding
import com.thekainchee.user.presentation.booking.model.BookingDetailUiModel
import com.thekainchee.user.utils.DateFormatter


class ShareBookingBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentShareBookingBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingData: BookingDetailUiModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBookingBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingData = requireArguments().getParcelable(
            ARG_BOOKING_DATA
        )!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupParlourDetails()
        setupBookingSchedule()
        setupServiceDetails()
        setupBookingInfo()
        setupBookingStatus()
        setupPaymentStatus()
        binding.ivCopyBookingId.setOnClickListener {
            copyBookingId()
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.btnShareBooking.setOnClickListener {
            shareBooking()
        }
    }

    companion object {

        private const val ARG_BOOKING_DATA = "arg_booking_data"

        fun newInstance(
            bookingData: BookingDetailUiModel
        ): ShareBookingBottomSheet {

            return ShareBookingBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_BOOKING_DATA, bookingData)
                }
            }
        }
    }
    private fun setupParlourDetails() {

        binding.tvParlourName.text = bookingData.parlourName

        binding.tvParlourAddress.text = buildString {
            val location = bookingData.location

            append(
                listOfNotNull(
                    location?.manualAddress?.details,
                    location?.manualAddress?.landmark,
                    location?.address?.city,
                    location?.address?.state
                ).joinToString(", ")
            )
        }

        bookingData.parlourImages
            ?.firstOrNull()
            ?.let { imageUrl ->

                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_oops)
                    .error(R.drawable.ic_oops)
                    .into(binding.ivParlourImage)
            }
    }
    private fun setupBookingSchedule() {

        binding.tvBookingDate.text =
            DateFormatter.formatBookingSuccessDate(
                bookingData.bookingDate
            )

        binding.tvBookingDay.text =
            DateFormatter.formatBookingDay(
                bookingData.bookingDate
            )

        binding.tvBookingTime.text =
            "${bookingData.slotStartTime} – ${bookingData.slotEndTime}"

        binding.tvBookingDuration.text =
            DateFormatter.formatDuration(
                bookingData.totalDurationMinutes
            )

        binding.tvStaffName.text =
            bookingData.staffName
    }
    private fun setupServiceDetails() {

        bookingData.services.firstOrNull()?.let { service ->
            val remainingServices = bookingData.services.size - 1

            binding.tvServiceName.text  =
                if (remainingServices > 0) {
                    "${service.name} + $remainingServices"
                } else {
                    service.name
                }

            binding.tvServiceDuration.text =
                DateFormatter.formatDuration(bookingData.totalDurationMinutes)

            Glide.with(this)
                .load(service.image)
                .placeholder(R.drawable.ic_oops)
                .error(R.drawable.ic_oops)
                .into(binding.ivServiceImage)
        }


        binding.tvTotalServicePrice.text =
            "₹${bookingData.totalPrice}"
    }
    private fun setupBookingInfo() {

        binding.tvBookingId.text =
            "#${bookingData.bookingId.takeLast(8)}"

    }

    private fun setupBookingStatus() {

        when (bookingData.bookingStatus) {

            "PENDING" -> {
                binding.tvBookingStatus.text = "Booking Pending"

                binding.tvBookingStatus.setTextColor(
                    Color.parseColor("#B45309")
                )

                binding.bookingStatusChip.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_booking_pending
                    )

                binding.ivBookingStatus.setImageResource(
                    R.drawable.ic_booking_status_pending_icon
                )
            }

            "CONFIRMED" -> {
                binding.tvBookingStatus.text = "Booking Confirmed"

                binding.tvBookingStatus.setTextColor(
                    Color.parseColor("#087F5B")
                )

                binding.bookingStatusChip.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_status_confirmed
                    )

                binding.ivBookingStatus.setImageResource(
                    R.drawable.ic_booking_status_confirmed_icon
                )
            }

            "CHECKED_IN" -> {
                binding.tvBookingStatus.text = "Checked In"

                binding.tvBookingStatus.setTextColor(
                    Color.parseColor("#2563EB")
                )

                binding.bookingStatusChip.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_status_checked_in
                    )

                binding.ivBookingStatus.setImageResource(
                    R.drawable.ic_booking_status_checked_in_icon
                )
            }

            "COMPLETED" -> {
                binding.tvBookingStatus.text = "Booking Completed"

                binding.tvBookingStatus.setTextColor(
                    Color.parseColor("#087F5B")
                )

                binding.bookingStatusChip.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_status_completed
                    )

                binding.ivBookingStatus.setImageResource(
                    R.drawable.ic_booking_status_completed_icon
                )
            }

            "CANCELLED" -> {
                binding.tvBookingStatus.text = "Booking Cancelled"

                binding.tvBookingStatus.setTextColor(
                    Color.parseColor("#D62828")
                )

                binding.bookingStatusChip.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_status_cancelled
                    )

                binding.ivBookingStatus.setImageResource(
                    R.drawable.ic_booking_status_cancelled_icon
                )
            }

            "NO_SHOW" -> {
                binding.tvBookingStatus.text = "No Show"

                binding.tvBookingStatus.setTextColor(
                    Color.parseColor("#B45309")
                )

                binding.bookingStatusChip.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_status_no_show
                    )

                binding.ivBookingStatus.setImageResource(
                    R.drawable.ic_booking_status_no_show_icon
                )
            }

            else -> {
                binding.tvBookingStatus.text = "Booking Status"

                binding.tvBookingStatus.setTextColor(
                    Color.parseColor("#4B5563")
                )

                binding.bookingStatusChip.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_status_default
                    )
            }
        }
    }


    private fun setupPaymentStatus() {

        when (bookingData.paymentStatus) {

            "PENDING" -> {
                binding.tvPaymentStatus.text = "Payment Pending"
                binding.tvPaymentStatus.setTextColor(
                    Color.parseColor("#B45309")
                )
                binding.tvPaymentStatus.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_payment_status_pending
                    )
            }

            "PAID" -> {
                binding.tvPaymentStatus.text = "Payment Successful"
                binding.tvPaymentStatus.setTextColor(
                    Color.parseColor("#087F5B")
                )
                binding.tvPaymentStatus.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_payment_status_success
                    )
            }

            "FAILED" -> {
                binding.tvPaymentStatus.text = "Payment Failed"
                binding.tvPaymentStatus.setTextColor(
                    Color.parseColor("#D62828")
                )
                binding.tvPaymentStatus.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_payment_status_failed
                    )
            }

            "REFUND_PENDING" -> {
                binding.tvPaymentStatus.text = "Refund Pending"
                binding.tvPaymentStatus.setTextColor(
                    Color.parseColor("#B45309")
                )
                binding.tvPaymentStatus.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_payment_status_pending
                    )
            }

            "REFUNDED" -> {
                binding.tvPaymentStatus.text = "Refunded"
                binding.tvPaymentStatus.setTextColor(
                    Color.parseColor("#087F5B")
                )
                binding.tvPaymentStatus.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_payment_status_success
                    )
            }

            "REFUND_TO_WALLET" -> {
                binding.tvPaymentStatus.text = "Refunded to Wallet"
                binding.tvPaymentStatus.setTextColor(
                    Color.parseColor("#087F5B")
                )
                binding.tvPaymentStatus.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_payment_status_success
                    )
            }

            else -> {
                binding.tvPaymentStatus.text = "Payment Status"
                binding.tvPaymentStatus.setTextColor(
                    Color.parseColor("#4B5563")
                )
                binding.tvPaymentStatus.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_payment_status_pending
                    )
            }
        }
    }
    private fun copyBookingId() {

        val bookingId = "#${bookingData.bookingId.takeLast(8)}"

        val clipboard =
            requireContext().getSystemService(
                Context.CLIPBOARD_SERVICE
            ) as ClipboardManager

        val clip = ClipData.newPlainText(
            "Booking ID",
            bookingId
        )

        clipboard.setPrimaryClip(clip)

    }
    private fun shareBooking() {

        val bookingId =
            "#${bookingData.bookingId.takeLast(8)}"

        val servicesText =
            if (bookingData.services.size > 1) {
                "${bookingData.services.first().name} + ${bookingData.services.size - 1} more services"
            } else {
                bookingData.services.firstOrNull()?.name ?: "Service"
            }

        val address = listOfNotNull(
            bookingData.location?.manualAddress?.details,
            bookingData.location?.manualAddress?.landmark,
            bookingData.location?.address?.city,
            bookingData.location?.address?.state
        ).joinToString(", ")

        val paymentStatus = when (bookingData.paymentStatus) {
            "PENDING" -> "Payment Pending"
            "PAID" -> "Payment Successful"
            "FAILED" -> "Payment Failed"
            "REFUND_PENDING" -> "Refund Pending"
            "REFUNDED" -> "Refunded"
            "REFUND_TO_WALLET" -> "Refunded to Wallet"
            else -> "Payment Status"
        }

        val bookingStatus = when (bookingData.bookingStatus) {
            "PENDING" -> "Booking Pending"
            "CONFIRMED" -> "Booking Confirmed"
            "CHECKED_IN" -> "Checked In"
            "COMPLETED" -> "Booking Completed"
            "CANCELLED" -> "Booking Cancelled"
            "NO_SHOW" -> "No Show"
            else -> "Booking Status"
        }
        val message = """
        ✨ My Booking – THE KAINCHEE

        ${bookingData.parlourName}
        $address

        📅 ${DateFormatter.formatBookingSuccessDate(bookingData.bookingDate)}
        🕐 ${bookingData.slotStartTime} – ${bookingData.slotEndTime}
        👤 ${bookingData.staffName}

        💇 $servicesText
        💰 Total Amount: ₹${bookingData.totalPrice}

        🧾 Booking ID: $bookingId
        💳 Payment: $paymentStatus

        Booking Status: $bookingStatus
    """.trimIndent()

        // Next step: Android Share Sheet
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }

        startActivity(
            Intent.createChooser(
                shareIntent,
                "Share Booking"
            )
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}