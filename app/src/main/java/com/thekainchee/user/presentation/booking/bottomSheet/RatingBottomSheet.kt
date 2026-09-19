package com.thekainchee.user.presentation.booking.bottomSheet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentRatingBottomSheetBinding
import com.thekainchee.user.presentation.booking.model.BookingDetailUiModel
import com.thekainchee.user.presentation.booking.state.RatingStatusState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.utils.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RatingBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentRatingBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingDetail: BookingDetailUiModel
    private val viewModel: BookingViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private var parlourRating = 0f
    private var staffRating = 0f
    private var isCreatingRating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookingDetail = requireArguments().getParcelable(
            ARG_BOOKING_DETAIL
        )!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRatingBottomSheetBinding.inflate(
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
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            Snackbar.make(
                binding.root,
                "No internet connection",
                Snackbar.LENGTH_SHORT
            ).show()

            dismiss()

        }else{
            viewModel.getRatingStatus(
                bookingDetail.bookingId
            )
        }
        setupRatingBars()
        setupClicks()
        checkRatingStatus()
    }

    private fun checkRatingStatus() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.ratingStatus.collectLatest { state ->

                    when (state) {

                        is RatingStatusState.Idle -> {
                            // kuch nahi
                        }

                        is RatingStatusState.Loading -> {

                            binding.layoutChecking.visibility = View.VISIBLE
                            binding.layoutRating.visibility = View.GONE
                            binding.layoutAlreadyRated.visibility = View.GONE
                            binding.layoutSubmitted.visibility = View.GONE
                            if(isCreatingRating){
                                binding.tvLoadingTitle.text= "Submitting Your Rating..."
                                binding.tvLoadingSubTitle.text= "Please wait while we save\n" +
                                        "your feedback."
                            }else{
                                binding.tvLoadingTitle.text= "Just a moment..."
                                binding.tvLoadingSubTitle.text= "Checking if you've already\n" +
                                        "rated this booking"
                            }
                        }

                        is RatingStatusState.NotRated -> {

                            binding.layoutChecking.visibility = View.GONE
                            binding.layoutRating.visibility = View.VISIBLE
                            binding.layoutAlreadyRated.visibility = View.GONE
                            binding.layoutSubmitted.visibility = View.GONE

                            // Booking detail ka data
                            binding.tvParlourName.text = bookingDetail.parlourName
                            binding.tvParlourLocation.text = buildString {
                                val location = bookingDetail.location
                                append(
                                    listOfNotNull(
                                        location?.manualAddress?.details,
                                        location?.manualAddress?.landmark,
                                        location?.address?.city,
                                        location?.address?.state
                                    ).joinToString(", ")
                                )
                            }
                            bookingDetail.parlourImages
                                ?.firstOrNull()
                                ?.let { imageUrl ->
                                    Glide.with(requireContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_oops)
                                        .error(R.drawable.ic_oops)
                                        .into(binding.ivParlour)
                                }
                        }

                        is RatingStatusState.AlreadyRated -> {

                            binding.layoutChecking.visibility = View.GONE
                            binding.layoutRating.visibility = View.GONE
                            binding.layoutAlreadyRated.visibility = View.VISIBLE
                            binding.layoutSubmitted.visibility = View.GONE

                            // Existing rating ka data yahin set karenge
                            val rating = state.data.rating

                            if (rating != null) {

                                // Parlour
                                binding.ratingBarPreviousParlour.rating =
                                    rating.parlourRating

                                binding.tvPreviousParlourRating.text =
                                    rating.parlourRating.toString()

                                // Staff
                                binding.ratingBarPreviousStaff.rating =
                                    rating.staffRating

                                binding.tvPreviousStaffRating.text =
                                    rating.staffRating.toString()
                            }
                        }
                        is RatingStatusState.Submitted -> {
                            Log.d("DISMISS", "Rating submitted successfully")
                            isCreatingRating = false
                            binding.layoutChecking.visibility = View.GONE
                            binding.layoutRating.visibility = View.GONE
                            binding.layoutAlreadyRated.visibility = View.GONE
                            binding.layoutSubmitted.visibility = View.VISIBLE
                        }

                        is RatingStatusState.Error -> {
                            Log.d(
                                "DISMISS",
                                "Dismissing due to error: ${state.message}"
                            )

                            isCreatingRating = false
                            Snackbar.make(
                                binding.root,
                                state.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }
    private fun setupClicks() {

        binding.btnCloseAlreadyRated.setOnClickListener {
            dismiss()
        }

        binding.btnSubmittedDone.setOnClickListener {
            dismiss()
        }

        binding.btnSubmitRating.setOnClickListener {

            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No internet connection",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }else{
                isCreatingRating = true
                viewModel.createRating(
                    bookingId = bookingDetail.bookingId,
                    parlourRating = parlourRating,
                    staffRating = staffRating,
                    review = binding.etReview.text
                        ?.toString()
                        ?.trim()
                        .orEmpty()
                )
            }
        }
        binding.ivCloseRating.setOnClickListener {
            dismiss()
        }
    }
    private fun setupRatingBars() {

        binding.ratingBarParlour.setOnRatingBarChangeListener { _, rating, _ ->
            parlourRating = rating
        }

        binding.ratingBarStaff.setOnRatingBarChangeListener { _, rating, _ ->
            staffRating = rating
        }
    }
    companion object {

        private const val ARG_BOOKING_DETAIL = "booking_detail"

        fun newInstance(
            bookingDetail: BookingDetailUiModel
        ): RatingBottomSheet {

            return RatingBottomSheet().apply {

                arguments = Bundle().apply {
                    putParcelable(
                        ARG_BOOKING_DETAIL,
                        bookingDetail
                    )
                }
            }
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}