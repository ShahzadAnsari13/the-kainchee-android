package com.thekainchee.user.presentation.booking.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentBookingDetailBinding
import com.thekainchee.user.presentation.booking.BookingActivity
import com.thekainchee.user.presentation.booking.state.BookingDetailUiState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
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
        if (!NetworkUtils.isInternetAvailable(requireContext())){
            binding.shimmerLayout.visibility = View.GONE
            binding.contentLayout.visibility = View.GONE
            binding.errorLayout.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }else{
            binding.layoutNoInternet.visibility = View.GONE
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.shimmerLayout.startShimmer()
            successViewModel.getBookingDetails(bookingId)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                successViewModel.bookingDetailState.collect {state->
                    when(state){
                        is BookingDetailUiState.Idle -> {
                        }
                        is BookingDetailUiState.Loading -> {
                            binding.shimmerLayout.visibility = View.VISIBLE
                            binding.contentLayout.visibility = View.GONE
                            binding.shimmerLayout.startShimmer()
                        }
                        is BookingDetailUiState.Success -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.contentLayout.visibility = View.VISIBLE
                            if(state.data.bookingStatus=="CONFIRMED"){
                                binding.tvBookingStatus.text = "✨ Booking Confirmed"
                            }else{
                                binding.tvBookingStatus.text  = state.data.bookingStatus
                            }
                            binding.tvBookedOn.text =  "${DateFormatter.formatBookingSuccessDate(state.data.createdAt)} • ⏱ ${state.data.slotStartTime}"
                            binding.tvBookingId.text =
                                "Booking #${state.data.bookingId.takeLast(8)}"
                            binding.tvParlourName.text  = state.data.parlourName
                            val location = state.data.location

                            binding.tvAddress.text =
                                "📍 ${
                                    listOfNotNull(
                                        location?.manualAddress?.details,
                                        location?.manualAddress?.landmark,
                                        location?.address?.city,
                                        location?.address?.state
                                    ).joinToString(", ")
                                }"

                            binding.tvPhone.text = state.data.parlourPhone
                            binding.tvStaffName.text = state.data.staffName
                            binding.tvAppointmentDate.text = "${DateFormatter.formatBookingSuccessDate(state.data.bookingDate)}"
                            binding.tvTimeSlot.text = "${state.data.slotStartTime} - ${state.data.slotEndTime}"
                            binding.tvServiceName.text = state.data.services.firstOrNull()?.name
                            state.data.services.firstOrNull()?.let { service ->

                                binding.tvServiceName.text = service.name

                                binding.tvServiceInfo.text =
                                    "${service.durationMinutes} min • ₹${service.price}"

                                Glide.with(requireContext())
                                    .load(service.image)
                                    .into(binding.ivService)

                            }
                            val serviceCount = state.data.services.size-1
                            if(serviceCount>=1){
                                binding.tvMoreServices.visibility = View.VISIBLE
                                binding.tvMoreServices.text = "+${state.data.services.size-1} More Services"
                            }else{
                                binding.tvMoreServices.visibility = View.GONE
                            }

                            binding.tvPaymentStatus.text = state.data.paymentStatus
                            binding.tvAmount.text = "₹${state.data.totalPrice}"
                            binding.tvPaymentMethod.text = state.data.paymentMethod

                            when(state.data.paymentStatus){

                                "PAID" -> {
                                    binding.btnCompletePayment.visibility = View.GONE
                                }

                                "PENDING" -> {
                                    binding.btnCompletePayment.visibility = View.VISIBLE
                                    binding.btnCompletePayment.text = "Complete Payment"
                                }

                                "FAILED" -> {
                                    binding.btnCompletePayment.visibility = View.VISIBLE
                                    binding.btnCompletePayment.text = "Retry Payment"
                                }
                            }

                        }
                        is BookingDetailUiState.Error -> {
                            binding.shimmerLayout.stopShimmer()
                            binding.shimmerLayout.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE

                        }
                    }
                }
            }
        }
        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root,"No Internet Connection",Snackbar.LENGTH_SHORT).show()
            }else{
                binding.layoutNoInternet.visibility = View.GONE
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.shimmerLayout.startShimmer()
                successViewModel.getBookingDetails(bookingId)
            }
        }
        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
                Snackbar.make(binding.root,"No Internet Connection",Snackbar.LENGTH_SHORT).show()
            }else{
                binding.errorLayout.visibility = View.GONE
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.shimmerLayout.startShimmer()
                successViewModel.getBookingDetails(bookingId)
            }
        }
    }



    override fun onResume() {
        super.onResume()

        (requireActivity() as BookingActivity)
            .showToolbar(false)
    }


    override fun onDestroyView() {
        super.onDestroyView()

        (requireActivity() as BookingActivity)
            .showToolbar(true)
        _binding = null
    }


}