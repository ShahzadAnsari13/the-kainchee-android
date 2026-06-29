package com.thekainchee.user.presentation.booking.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentBookingSuccessBinding
import com.thekainchee.user.presentation.booking.BookingActivity
import com.thekainchee.user.presentation.booking.state.BookingDetailUiState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.presentation.dashboard.DashboardActivity
import com.thekainchee.user.utils.DateFormatter
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookingSuccess : Fragment() {

    private var _binding: FragmentBookingSuccessBinding? = null
    private val binding get() = _binding!!
    private val navArgs : BookingSuccessArgs by navArgs()
    private val bookingId by lazy{
        navArgs.bookingId
    }
    private val successViewModel : BookingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingSuccessBinding.inflate(inflater,container,false)
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
                            binding.tvParlourName.text = state.data.parlourName
                            binding.tvDateTime.text =  "${DateFormatter.formatBookingSuccessDate(state.data.bookingDate)} • ⏱ ${state.data.slotStartTime}"
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
        binding.btnHome.setOnClickListener {

            val intent = Intent(
                requireContext(),
                DashboardActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)

            requireActivity().finish()
        }
        binding.btnViewBooking.setOnClickListener {
            findNavController().navigate(
                BookingSuccessDirections.actionBookingSuccessFragmentToMyBookingFragment()
            )
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
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {

            val intent = Intent(
                requireContext(),
                DashboardActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)

            requireActivity().finish()
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