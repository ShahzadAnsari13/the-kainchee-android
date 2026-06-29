package com.thekainchee.user.presentation.booking.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentMyBookingsBinding
import com.thekainchee.user.presentation.booking.BookingActivity
import com.thekainchee.user.presentation.booking.adapter.MyBookingsAdapter
import com.thekainchee.user.presentation.booking.state.MyBookingsUiState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class MyBookings : Fragment() {
    private var _binding : FragmentMyBookingsBinding? = null
    val binding get() = _binding!!
    private lateinit var bookingsAdapter: MyBookingsAdapter
    private var currentFilter = "UPCOMING"
    private val successViewModel : BookingViewModel by viewModels()
    private var isSwipeRefresh  = false
    private var isNoInternetLayoutVisible   =  false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBookingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingsAdapter = MyBookingsAdapter { booking ->
            // Navigate Booking Detail
             val action = MyBookingsDirections.myBookingFragmentToBookingDetailFragment(booking.bookingId)
            findNavController().navigate(action)
        }

        binding.rvBookings.apply {

            adapter = bookingsAdapter

            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)

        }
        binding.swipeRefresh.setOnRefreshListener {
            if (!checkInternetOrShowLayout()) return@setOnRefreshListener

                isSwipeRefresh  =  true
                successViewModel.getMyBookings(currentFilter)

        }
        binding.chipUpcoming.setOnClickListener {
            if (!checkInternetOrShowLayout()) return@setOnClickListener
                if (currentFilter != "UPCOMING") {
                    currentFilter = "UPCOMING"
                    successViewModel.getMyBookings(currentFilter)
                }


        }
        binding.chipCompleted.setOnClickListener {
            if (!checkInternetOrShowLayout()) return@setOnClickListener
                if (currentFilter != "COMPLETED") {
                    currentFilter = "COMPLETED"
                    successViewModel.getMyBookings(currentFilter)
                }


        }
        binding.chipCancelled.setOnClickListener {
            if (!checkInternetOrShowLayout()) return@setOnClickListener
                if (currentFilter != "CANCELLED") {
                    currentFilter = "CANCELLED"
                    successViewModel.getMyBookings(currentFilter)
                }


        }
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.swipeRefresh.visibility = View.GONE
            binding.shimmerLayout.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }else{
            binding.layoutNoInternet.visibility = View.GONE
            successViewModel.getMyBookings(currentFilter)
        }
        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root,"No Internet Connection",Snackbar.LENGTH_SHORT).show()
            }else{
                binding.layoutNoInternet.visibility = View.GONE
                successViewModel.getMyBookings(currentFilter)
            }
        }
        binding.btnRetry.setOnClickListener {
            if (!NetworkUtils.isInternetAvailable(requireContext())){
                binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                binding.errorLayout.visibility = View.GONE
                successViewModel.getMyBookings(currentFilter)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                successViewModel.myBookingsState.collect { state ->
                    when(state){
                        is MyBookingsUiState.Idle -> {
                        }
                        is MyBookingsUiState.Loading -> {
                            if(!isSwipeRefresh ){
                                binding.swipeRefresh.visibility = View.GONE
                                binding.errorLayout.visibility = View.GONE
                                binding.layoutNoInternet.visibility = View.GONE
                                isNoInternetLayoutVisible  = false
                                binding.shimmerLayout.visibility = View.VISIBLE
                                binding.shimmerLayout.startShimmer()
                            }
                        }
                        is MyBookingsUiState.Success -> {
                            if(!isSwipeRefresh ){
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE
                                binding.swipeRefresh.visibility = View.VISIBLE

                            }else{
                                binding.swipeRefresh.isRefreshing = false
                                isSwipeRefresh  = false
                            }
                            bookingsAdapter.submitList(state.bookings)
                        }
                        is MyBookingsUiState.Error -> {

                            if(!isSwipeRefresh ){
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE

                            }else{
                                binding.swipeRefresh.isRefreshing = false
                                isSwipeRefresh  = false
                            }
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.tvEmptyTitle.text = "Unable to Load"
                            binding.tvEmptySubtitle.text = "We couldn't load your bookings. Please try again."
                            binding.ivEmpty.setImageResource(R.drawable.error_img)
                        }
                        is MyBookingsUiState.Empty -> {
                            if(!isSwipeRefresh ){
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE

                            }else{
                                binding.swipeRefresh.isRefreshing = false

                                isSwipeRefresh  = false
                                //binding.emptyLayout.visibility = View.VISIBLE
                            }
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.tvEmptyTitle.text="No Upcoming Bookings"
                            binding.tvEmptySubtitle.text = "Book your next appointment to see it here."
                            binding.ivEmpty.setImageResource(R.drawable.ic_no_data)
                            bookingsAdapter.submitList(emptyList())
                        }
                    }
                }
            }
        }
    }
    private fun checkInternetOrShowLayout(): Boolean {

        if (NetworkUtils.isInternetAvailable(requireContext())) {
            return true
        }

        if (!isNoInternetLayoutVisible ) {
            binding.layoutNoInternet.visibility = View.VISIBLE
            isNoInternetLayoutVisible  = true
        } else {
            Snackbar.make(
                binding.root,
                "No Internet Connection",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        return false
    }
    override fun onResume() {
        super.onResume()

        (requireActivity() as BookingActivity)
            .setToolbarTitle("My Bookings")
    }



    override fun onDestroyView() {
        super.onDestroyView()

        (requireActivity() as BookingActivity)
            .showToolbar(true)
        binding.rvBookings.adapter = null
        _binding = null
    }

}