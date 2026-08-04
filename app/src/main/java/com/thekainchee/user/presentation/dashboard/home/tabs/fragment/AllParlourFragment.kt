package com.thekainchee.user.presentation.dashboard.home.tabs.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentAllParlourBinding
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.dashboard.home.adapter.ParlourHorizontalAdapter
import com.thekainchee.user.presentation.dashboard.home.adapter.TrendingServiceAdapter
import com.thekainchee.user.presentation.dashboard.home.adapter.UpcomingBookingAdapter
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.state.BookingState
import com.thekainchee.user.presentation.dashboard.home.state.LocationUiState
import com.thekainchee.user.presentation.dashboard.home.state.ParlourState
import com.thekainchee.user.presentation.dashboard.home.state.TrendingServiceState
import com.thekainchee.user.presentation.dashboard.home.viewModel.LocationViewModel
import com.thekainchee.user.presentation.dashboard.home.viewModel.ParlourViewModel
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllParlourFragment : Fragment() {
    private var _binding: FragmentAllParlourBinding? = null
    private val binding get() = _binding!!
    private lateinit var nearbyAdapter: ParlourHorizontalAdapter
    private lateinit var trendingAdapter: ParlourHorizontalAdapter

    private lateinit var trendingServiceAdapter: TrendingServiceAdapter
    private lateinit var upcomingBookingAdapter: UpcomingBookingAdapter
    private  val locationViewModel : LocationViewModel by activityViewModels()
    private val parlourViewModel : ParlourViewModel by viewModels()
    private var lastLat: Double? = null
    private var lastLng: Double? = null
    private var lat: Double? = null
    private var lng: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllParlourBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupPagination()
        observeUiStates()

        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.mainContent.visibility = View.GONE
            showNoInternetState(retryText = "Try Again") {
                if (!NetworkUtils.isInternetAvailable(requireContext())) {
                    Snackbar.make(
                        binding.root,
                        "No Internet Connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    binding.stateView.hide()
                    if (lat != null && lng != null) {
                        retryAllData()
                    } else {
                        locationViewModel.fetchUserLocation()
                    }
                }
            }
            return
        }

    }
    private fun observeUiStates(){
        observeLocation()
        observeNearby()
        observeTrending()
        observeTrendingServices()
        observeBookings()
    }
    private fun retryAllData() {

        showMainLoading()
        parlourViewModel.getNearbyParlours(
            type = null,
            forceRefresh = true
        )
        parlourViewModel.trendingParlours(type = null)

        parlourViewModel.trendingServices()

        parlourViewModel.upcomingBookings()
    }
    private fun showFullEmpty() {
        binding.mainContent.isVisible = false
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "No Parlour Found",
                subtitle = "No parlours found in your area.",
                primaryButtonText = "Retry",
                onPrimaryClick = {
                    withInternet {
                        binding.stateView.hide()
                        retryAllData()
                    }
                }
            )
        )
    }
    private fun hideFullEmpty() {
        binding.mainContent.isVisible = true
        binding.stateView.hide()
    }

    private fun setupRecyclerViews(){
        binding.rvNearbyParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendingServices.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingBookings.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        nearbyAdapter = ParlourHorizontalAdapter (  onItemClick = ::openParlour)

        trendingAdapter = ParlourHorizontalAdapter ( onItemClick = ::openParlour)

        trendingServiceAdapter = TrendingServiceAdapter(onItemClick = {

        })

        upcomingBookingAdapter = UpcomingBookingAdapter(onItemClick = {

        })

        binding.rvNearbyParlours.adapter = nearbyAdapter
        binding.rvTrendingParlours.adapter = trendingAdapter
        binding.rvTrendingServices.adapter = trendingServiceAdapter
        binding.rvUpcomingBookings.adapter = upcomingBookingAdapter
    }

    private fun setupPagination(){
        binding.rvNearbyParlours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (dx >0 && lastVisibleItem >= totalItemCount - 2) {
                    parlourViewModel.nearbyLoadNextPage(type = null)
                }
            }
        })
    }

    private fun observeLocation(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.location.collect { state ->
                    when(state){
                        is LocationUiState.Idle -> {
                        }
                        is LocationUiState.Loading -> {
                            showMainLoading()
                        }
                        is LocationUiState.Success -> {
                            val currentLat = state.address.latitude
                            val currentLng = state.address.longitude
                            lat = currentLat
                            lng = currentLng
                            parlourViewModel.setLocation(currentLat, currentLng)

                            if(!NetworkUtils.isInternetAvailable(requireContext())){
                                hideMainLoading()
                                showNoInternetState {
                                    if (!NetworkUtils.isInternetAvailable(requireContext())) {
                                        Snackbar.make(
                                            binding.root,
                                            "No Internet Connection",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        binding.stateView.hide()
                                        if (lastLat != currentLat || lastLng != currentLng) {
                                            lastLat = currentLat
                                            lastLng = currentLng

                                            parlourViewModel.getNearbyParlours(type = null)
                                            parlourViewModel.trendingParlours(type = null)
                                            parlourViewModel.trendingServices()
                                            parlourViewModel.upcomingBookings()
                                        } else {
                                            binding.mainContent.isVisible = true
                                        }
                                    }
                                }
                            }else{
                                if (lastLat != currentLat || lastLng != currentLng) {
                                    lastLat = currentLat
                                    lastLng = currentLng
                                    parlourViewModel.getNearbyParlours(type = null)
                                    parlourViewModel.trendingParlours(type = null)
                                    parlourViewModel.trendingServices()
                                    parlourViewModel.upcomingBookings()
                                }else{
                                    hideMainLoading()
                                    binding.mainContent.isVisible = true
                                }

                            }
                        }
                        is LocationUiState.Error -> {
                            hideMainLoading()
                            binding.mainContent.isVisible = false
                            showLocationError(
                                onRetry = {
                                    withInternet {
                                        retryAllData()
                                    }
                                },
                                onChangeLocation = {
                                    openLocationScreen()
                                }
                            )
                        }


                    }
                }
            }
        }
    }
    private fun observeNearby(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parlourViewModel.nearbyParlourState.collect { state ->
                    when (state) {

                        is ParlourState.Loading -> {
                            binding.stateView.hide()
                            binding.mainContent.isVisible = false
                            binding.loaderNearby.isVisible = true
                        }

                        is ParlourState.Success -> {
                            hideMainLoading()
                            binding.mainContent.isVisible = true
                            binding.loaderNearby.isVisible = false
                            binding.layoutNearbySection.isVisible = state.data.isNotEmpty()

                            if (state.data.isEmpty()) {
                                showFullEmpty()
                            } else {
                                hideFullEmpty()
                            }

                            nearbyAdapter.submitList(state.data)
                        }

                        is ParlourState.Error -> {

                            hideMainLoading()
                            binding.loaderNearby.isVisible = false
                            binding.layoutNearbySection.isVisible = false
                            binding.mainContent.isVisible = false
                            showNearbyError(
                                onRetry = {
                                    withInternet {
                                        binding.stateView.hide()
                                        retryAllData()
                                    }
                                },
                                onChangeLocation = {
                                    openLocationScreen()
                                }
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeTrending(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parlourViewModel.trendingParlourState.collect { state ->
                    when (state){
                        is ParlourState.Loading -> {
                            binding.layoutTrendingParloursSection.isVisible = true
                            binding.loaderTrendingParlour.isVisible = true
                        }
                        is ParlourState.Success ->{
                            binding.loaderTrendingParlour.isVisible = false
                            binding.layoutTrendingParloursSection.isVisible = state.data.isNotEmpty()
                            trendingAdapter.submitList(state.data)
                        }
                        is ParlourState.Error->{
                            binding.layoutTrendingParloursSection.isVisible = false
                            binding.loaderTrendingParlour.isVisible = false
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeTrendingServices(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parlourViewModel.trendingServiceState.collect { state ->
                    when(state){
                        is TrendingServiceState.Loading -> {
                            binding.layoutTrendingServicesSection.isVisible = true
                            binding.loaderTrendingService.isVisible = true

                        }
                        is TrendingServiceState.Success -> {

                            binding.loaderTrendingService.isVisible = false
                            binding.layoutTrendingServicesSection.isVisible = state.data.isNotEmpty()
                            trendingServiceAdapter.submitList(state.data)
                        }
                        is TrendingServiceState.Error -> {
                            binding.layoutTrendingServicesSection.isVisible = false
                            binding.loaderTrendingService.isVisible = false

                        }else -> Unit
                    }
                }
            }
        }
    }

    private fun observeBookings(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parlourViewModel.bookingState.collect { state ->
                    when(state){
                        is BookingState.Loading -> {
                            binding.layoutBookingsSection.isVisible = true
                            binding.loaderUpcomingBooking.isVisible = true
                        }
                        is BookingState.Success -> {

                            binding.loaderUpcomingBooking.isVisible = false
                            binding.layoutBookingsSection.isVisible = state.data.isNotEmpty()
                            upcomingBookingAdapter.submitList(state.data)
                        }
                        is BookingState.Error -> {

                            binding.layoutBookingsSection.isVisible = false

                            binding.loaderUpcomingBooking.isVisible = false

                        }else -> Unit
                    }
                }
            }
        }
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
    private fun showLocationError(
        onRetry: () -> Unit,
        onChangeLocation: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_no_loc,
                title = "Location unavailable",
                subtitle = "We couldn't access your location.\nPlease retry from the top location bar.",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry,
                secondaryButtonText = "Change Location",
                onSecondaryClick = onChangeLocation
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

    private fun showNearbyError(
        onRetry: () -> Unit,
        onChangeLocation: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "Unable to load parlours",
                subtitle = "Something went wrong while loading nearby parlours.\nPlease retry or change your location",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry,
                secondaryButtonText = "Change Location",
                onSecondaryClick = onChangeLocation
            )
        )
    }
    private fun showMainLoading() {
        binding.shimmerLayout.isVisible = true
        binding.shimmerLayout.startShimmer()
        binding.mainContent.isVisible = false
        binding.stateView.root.isVisible = false
    }
    private fun hideMainLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.isVisible = false
    }
    private fun openParlour(item: ParlourUI) {
        startActivity(
            Intent(requireContext(), ParlourActivity::class.java).apply {
                putExtra("parlourId", item.id)
                putExtra("distance", item.distance.toString())
            }
        )
    }
    private fun openLocationScreen() {
        startActivity(
            Intent(requireContext(), LocationActivity::class.java)
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}