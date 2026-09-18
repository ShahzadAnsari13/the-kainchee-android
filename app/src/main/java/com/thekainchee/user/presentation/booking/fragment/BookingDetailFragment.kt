package com.thekainchee.user.presentation.booking.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentBookingDetailBinding
import com.thekainchee.user.presentation.booking.BookingActivity
import com.thekainchee.user.presentation.booking.bottomSheet.CancelBookingBottomSheet
import com.thekainchee.user.presentation.booking.bottomSheet.ServicesBottomSheet
import com.thekainchee.user.presentation.booking.bottomSheet.ShareBookingBottomSheet
import com.thekainchee.user.presentation.booking.model.BookingDetailUiModel
import com.thekainchee.user.presentation.booking.state.BookingDetailUiState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.parlour.fragment.ParlourDetailFragmentDirections
import com.thekainchee.user.utils.DateFormatter
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class BookingDetailFragment : Fragment() {
    private var _binding : FragmentBookingDetailBinding? = null
    val binding get() = _binding!!
    private val navArgs : BookingDetailFragmentArgs by navArgs()
    private val bookingId by lazy{
        navArgs.bookingId
    }
    private var latitude: String? = null
    private var longitude: String? = null
    private var bookingData: BookingDetailUiModel? = null
    private val successViewModel : BookingViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookingDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BookingActivity).apply {
            showToolbar(true)
            setToolbarTitle("Booking Details")
        }
        observer()
        clickListeners()
        withInternet {
            successViewModel.getBookingDetails(bookingId)
        }
    }
    private fun clickListeners(){
        binding.btnDirections.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }else {
                latitude?.let { lat ->
                    longitude?.let { lng ->
                        val action =
                            BookingDetailFragmentDirections.actionBookingDetailFragmentToParlourMap(
                                latitude = lat,
                                longitude = lng
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
        binding.btnShare.setOnClickListener {
            bookingData?.let {
                ShareBookingBottomSheet
                    .newInstance(it)
                    .show(parentFragmentManager, "ShareBookingBottomSheet")
            }

        }
        binding.tvViewAllServices.setOnClickListener {
            bookingData?.let {
                ServicesBottomSheet
                    .newInstance(it)
                    .show(childFragmentManager, "ServicesBottomSheet")
            }

        }
        binding.btnCancelBooking.setOnClickListener {

            CancelBookingBottomSheet
                .newInstance(bookingId)
                .show(childFragmentManager, "CancelBookingBottomSheet")
        }
        binding.btnBookAgain.setOnClickListener {

        }
    }
    private fun observer(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                successViewModel.bookingDetailState.collect {state->
                    when(state){
                        is BookingDetailUiState.Idle -> {
                        }
                        is BookingDetailUiState.Loading -> {
                            binding.shimmerLayout.visibility = View.VISIBLE
                            binding.scrollView.visibility = View.GONE
                            binding.shimmerLayout.startShimmer()
                        }
                        is BookingDetailUiState.Success -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.scrollView.visibility = View.VISIBLE
                            val data = state.data
                            bookingData = data
                            longitude = data.location?.coordinates[0].toString()
                            latitude = data.location?.coordinates[1].toString()
                            // ================= HERO =================

                            binding.tvParlourName.text = data.parlourName
                            binding.tvParlourAddress.text = buildString {
                                val location = data.location

                                append(
                                    listOfNotNull(
                                        location?.manualAddress?.details,
                                        location?.manualAddress?.landmark,
                                        location?.address?.city,
                                        location?.address?.state
                                    ).joinToString(", ")
                                )
                            }
                            data.parlourImages
                                ?.firstOrNull()
                                ?.let { imageUrl ->
                                    Glide.with(requireContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_oops)
                                        .error(R.drawable.ic_oops)
                                        .into(binding.ivParlourImage)
                                }
                            // ================= APPOINTMENT =================

                            binding.tvBookingDate.text =
                                DateFormatter.formatBookingSuccessDate(data.bookingDate)

                            binding.tvBookingDay.text =
                                DateFormatter.formatBookingDay(data.bookingDate)

                            binding.tvBookingTime.text =
                                "${data.slotStartTime} – ${data.slotEndTime}"

                            binding.tvBookingDuration.text =
                                DateFormatter.formatDuration(data.totalDurationMinutes)

                            binding.tvStaffName.text = data.staffName
                            // ================= SERVICE =================

                            data.services.firstOrNull()?.let { service ->

                                binding.tvServiceName.text = service.name

                                binding.tvServiceDuration.text =
                                    "${service.durationMinutes} mins"

                                binding.tvServicePrice.text =
                                    "₹${service.price}"

                                Glide.with(requireContext())
                                    .load(service.image)
                                    .placeholder(R.drawable.ic_oops)
                                    .error(R.drawable.ic_oops)
                                    .into(binding.ivServiceImage)
                            }
                            if (data.services.size > 1) {
                                binding.tvViewAllServices.visibility = View.VISIBLE
                            } else {
                                binding.tvViewAllServices.visibility = View.GONE
                            }


                            // ================= PAYMENT =================

                            binding.tvTotalAmount.text =
                                "₹${data.totalPrice}"

                            binding.tvPaymentMethod.text =
                                data.paymentMethod ?: "—"

                            binding.tvPaymentStatus.text =
                                data.paymentStatus

                            // ================= BOOKING INFO =================

                            binding.tvBookingId.text =
                                "#${data.bookingId.takeLast(8)}"

                            binding.tvCreatedAt.text =
                                DateFormatter.formatBookingSuccessDate(data.createdAt)


                            // ================= STATUS =================

                            setupBookingStatus(data)

                        }
                        is BookingDetailUiState.Error -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            showBookingDetailsError {
                                successViewModel.getBookingDetails(bookingId)
                            }

                        }
                    }
                }
            }
        }
    }
    private fun setupBookingStatus(data: BookingDetailUiModel) {

        when (data.bookingStatus) {

            "PENDING" -> {

                binding.statusCard.setCardBackgroundColor(
                    Color.parseColor("#FFF4E5")

                )
                binding.ivStatusIcon.setImageResource(
                    R.drawable.ic_booking_status_pending_icon
                )

                binding.ivStatusIcon.setBackgroundResource(
                    R.drawable.bg_booking_status_pending_icon
                )
                binding.tvBookingStatusTitle.text = "Booking Pending"
                binding.tvBookingStatusTitle.setTextColor(
                    Color.parseColor("#B45309")
                )
                binding.tvBookingStatusMessage.text =
                    "Your booking is waiting for confirmation."

                binding.tvPendingCheck.text = "✓"

                binding.tvPendingCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_pending
                )

                // Cancel Booking visible
                binding.btnCancelBooking.visibility = View.VISIBLE

                // Book Again hidden
                binding.btnBookAgain.visibility = View.GONE
            }

            "CONFIRMED" -> {
                binding.statusCard.setCardBackgroundColor(
                    Color.parseColor("#E8F8F1")
                )
                binding.ivStatusIcon.setImageResource(
                    R.drawable.ic_booking_status_confirmed_icon
                )

                binding.ivStatusIcon.setBackgroundResource(
                    R.drawable.bg_booking_status_confirmed_icon
                )
                binding.tvBookingStatusTitle.text = "Booking Confirmed"
                binding.tvBookingStatusTitle.setTextColor(
                    Color.parseColor("#087F5B")
                )
                binding.tvBookingStatusMessage.text =
                    "Your appointment is confirmed."

                binding.tvPendingCheck.text = "✓"
                binding.tvPendingCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_pending
                )

                binding.tvConfirmedCheck.text = "✓"
                binding.tvConfirmedCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_confirmed
                )
                // Cancel Booking visible
                binding.btnCancelBooking.visibility = View.VISIBLE

                // Book Again hidden
                binding.btnBookAgain.visibility = View.GONE
            }

            "CHECKED_IN" -> {
                binding.statusCard.setCardBackgroundColor(
                    Color.parseColor("#E8F1FF")
                )
                binding.ivStatusIcon.setImageResource(
                    R.drawable.ic_booking_status_checked_in_icon
                )

                binding.ivStatusIcon.setBackgroundResource(
                    R.drawable.bg_booking_status_checked_in_icon
                )
                binding.tvBookingStatusTitle.text = "Checked In"
                binding.tvBookingStatusTitle.setTextColor(
                    Color.parseColor("#2563EB")
                )
                binding.tvBookingStatusMessage.text =
                    "You have checked in at the parlour."
                binding.tvPendingCheck.text = "✓"
                binding.tvPendingCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_pending
                )

                binding.tvConfirmedCheck.text = "✓"
                binding.tvConfirmedCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_confirmed
                )

                binding.tvCheckedInCheck.text = "✓"
                binding.tvCheckedInCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_checked_in
                )
                binding.btnCancelBooking.visibility = View.GONE
                binding.btnBookAgain.visibility = View.GONE
            }

            "COMPLETED" -> {
                binding.statusCard.setCardBackgroundColor(
                    Color.parseColor("#E8F8F1")
                )
                binding.ivStatusIcon.setImageResource(
                    R.drawable.ic_booking_status_completed_icon
                )

                binding.ivStatusIcon.setBackgroundResource(
                    R.drawable.bg_booking_status_completed_icon
                )
                binding.tvBookingStatusTitle.text = "Booking Completed"
                binding.tvBookingStatusTitle.setTextColor(
                    Color.parseColor("#087F5B")
                )
                binding.tvBookingStatusMessage.text =
                    "Your appointment has been completed."
                binding.tvPendingCheck.text = "✓"
                binding.tvPendingCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_pending
                )

                binding.tvConfirmedCheck.text = "✓"
                binding.tvConfirmedCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_confirmed
                )

                binding.tvCheckedInCheck.text = "✓"

                binding.tvCheckedInCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_checked_in
                )
                binding.tvCompletedCheck.text = "✓"


                binding.tvCompletedCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_confirmed
                )
                binding.btnCancelBooking.visibility = View.GONE
                binding.btnBookAgain.visibility = View.VISIBLE
            }

            "CANCELLED" -> {
                // Status card
                binding.statusCard.setCardBackgroundColor(
                    Color.parseColor("#FFF0F1")
                )
                binding.ivStatusIcon.setImageResource(
                    R.drawable.ic_booking_status_cancelled_icon
                )

                binding.ivStatusIcon.setBackgroundResource(
                    R.drawable.bg_booking_status_cancelled_icon
                )
                binding.tvBookingStatusTitle.text = "Booking Cancelled"
                binding.tvBookingStatusTitle.setTextColor(
                    Color.parseColor("#D62828")
                )
                binding.tvBookingStatusMessage.text =
                    data.cancelReason ?: "This booking has been cancelled."

                binding.tvPendingCheck.text = "✓"
                binding.tvPendingCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_pending
                )

                binding.tvConfirmedCheck.text = "✓"
                binding.tvConfirmedCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_confirmed
                )
                binding.tvCheckedInLabel.text = "Cancelled"

                binding.tvCheckedInCheck.text = "×"
                binding.tvCheckedInCheck.setTextColor(Color.WHITE)
                binding.tvCheckedInCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_cancelled
                )
                // Completed → Hidden
                binding.statusCompleted.visibility = View.GONE

                binding.lineCheckedInCompleted.visibility = View.GONE

                // Buttons
                binding.btnCancelBooking.visibility = View.GONE
                binding.btnBookAgain.visibility = View.VISIBLE
            }

            "NO_SHOW" -> {

                // Status card
                binding.statusCard.setCardBackgroundColor(
                    Color.parseColor("#FFF4E5")
                )
                binding.ivStatusIcon.setImageResource(
                    R.drawable.ic_booking_status_no_show_icon
                )

                binding.ivStatusIcon.setBackgroundResource(
                    R.drawable.bg_booking_status_no_show_icon
                )

                binding.tvBookingStatusTitle.text = "No Show"

                binding.tvBookingStatusTitle.setTextColor(
                    Color.parseColor("#B45309")
                )

                binding.tvBookingStatusMessage.text =
                    data.noShowReason ?: "This booking was marked as no show."

                // Pending
                binding.tvPendingCheck.text = "✓"
                binding.tvPendingCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_pending
                )

                // Confirmed
                binding.tvConfirmedCheck.text = "✓"
                binding.tvConfirmedCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_confirmed
                )

                // Checked In → No Show

                binding.tvCheckedInLabel.text = "No Show"

                binding.tvCheckedInCheck.text = "!"
                binding.tvCheckedInCheck.setTextColor(Color.WHITE)
                binding.tvCheckedInCheck.setBackgroundResource(
                    R.drawable.bg_booking_check_status_no_show
                )

                // Completed → Hidden
                binding.statusCompleted.visibility = View.GONE

                binding.lineCheckedInCompleted.visibility = View.GONE

                // Buttons
                binding.btnCancelBooking.visibility = View.GONE
                binding.btnBookAgain.visibility = View.VISIBLE
            }
        }
    }

    private fun showBookingDetailsError(
        onRetry: ()->Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.error_img,
                title = "Unable to Load",
                subtitle = "We couldn't load your booking details. Please try again.",
                primaryButtonText = "Retry",
                onPrimaryClick =onRetry
            )
        )
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

        (requireActivity() as BookingActivity)
            .setToolbarTitle("Booking Details")
        _binding = null
    }


}