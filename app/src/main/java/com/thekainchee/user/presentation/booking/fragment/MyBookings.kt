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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentMyBookingsBinding
import com.thekainchee.user.presentation.booking.BookingActivity
import com.thekainchee.user.presentation.booking.adapter.MyBookingsAdapter
import com.thekainchee.user.presentation.booking.model.MyBookingUiModel
import com.thekainchee.user.presentation.booking.state.MyBookingsUiState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class MyBookings : Fragment() {
    private var _binding : FragmentMyBookingsBinding? = null
    val binding get() = _binding!!
    private lateinit var bookingsAdapter: MyBookingsAdapter
    private var allBookings = emptyList<MyBookingUiModel>()
    private var currentFilter = "UPCOMING"
    private val successViewModel : BookingViewModel by viewModels()
    private var isSwipeRefresh  = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBookingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupFilterChips()
        observeBookings()
        withInternet {
            successViewModel.getMyBookings()
        }

    }
    private fun setupToolbar() {
        (requireActivity() as BookingActivity).apply {
            showToolbar(true)
            setToolbarTitle("My Bookings")
        }
    }
    private fun setupRecyclerView() {
        bookingsAdapter = MyBookingsAdapter { booking ->
            val action =
                MyBookingsDirections.myBookingFragmentToBookingDetailFragment(
                    booking.bookingId
                )
            findNavController().navigate(action)
        }

        binding.rvBookings.apply {
            adapter = bookingsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            withInternet {
                isSwipeRefresh = true
                successViewModel.getMyBookings()
            }
        }
    }
    private fun setupFilterChips() {
        binding.chipUpcoming.setOnClickListener {
            selectFilter("UPCOMING")
        }

        binding.chipCompleted.setOnClickListener {
            selectFilter("COMPLETED")
        }

        binding.chipCancelled.setOnClickListener {
            selectFilter("CANCELLED")
        }
    }
    private fun selectFilter(filter: String) {
        if (currentFilter == filter) return

        currentFilter = filter
        showFilteredBookings()
    }
    private fun observeBookings() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                successViewModel.myBookingsState.collect { state ->
                    when(state){
                        is MyBookingsUiState.Idle -> {
                        }
                        is MyBookingsUiState.Loading -> {
                            if(!isSwipeRefresh ){
                                binding.swipeRefresh.visibility = View.GONE
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
                            allBookings = state.bookings
                            showFilteredBookings()
                        }
                        is MyBookingsUiState.Error -> {

                            if(!isSwipeRefresh ){
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE

                            }else{
                                binding.swipeRefresh.isRefreshing = false
                                isSwipeRefresh  = false
                            }
                            showBookingsError{
                                withInternet {
                                    successViewModel.getMyBookings()
                                }
                            }
                        }
                        is MyBookingsUiState.Empty -> {
                            if(!isSwipeRefresh ){
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE

                            }else{
                                binding.swipeRefresh.isRefreshing = false

                                isSwipeRefresh  = false
                            }
                            bookingsAdapter.submitList(emptyList())
                            showBookingsEmpty()
                        }
                    }
                }
            }
        }
    }

    private fun showFilteredBookings() {

        val filteredBookings = when (currentFilter) {

            "UPCOMING" -> {
                allBookings.filter {
                    it.bookingStatus in listOf(
                        "PENDING",
                        "CONFIRMED",
                        "CHECKED_IN"
                    )
                }
            }

            "COMPLETED" -> {
                allBookings.filter {
                    it.bookingStatus == "COMPLETED"
                }
            }

            "CANCELLED" -> {
                allBookings.filter {
                    it.bookingStatus in listOf(
                        "CANCELLED",
                        "NO_SHOW"
                    )
                }
            }

            else -> allBookings
        }

        if (filteredBookings.isEmpty()) {
            bookingsAdapter.submitList(emptyList())
            showBookingsEmpty()
        } else {
            binding.stateView.hide()
            bookingsAdapter.submitList(filteredBookings)
        }
    }

    private fun showBookingsError(
        onRetry: ()->Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.error_img,
                title = "Unable to Load",
                subtitle = "We couldn't load your bookings. Please try again.",
                primaryButtonText = "Retry",
                onPrimaryClick =onRetry
            )
        )
    }
    private fun showBookingsEmpty(
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "No Bookings",
                subtitle = "Book your next appointment to see it here."
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