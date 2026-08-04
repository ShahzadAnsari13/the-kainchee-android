package com.thekainchee.user.presentation.dashboard.home.tabs.fragment

import android.content.Intent
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
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
import com.thekainchee.user.presentation.dashboard.home.adapter.ParlourVerticalAdapter
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.state.LocationUiState
import com.thekainchee.user.presentation.dashboard.home.state.ParlourState
import com.thekainchee.user.presentation.dashboard.home.viewModel.LocationViewModel
import com.thekainchee.user.presentation.dashboard.home.viewModel.ParlourViewModel
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint

class MensParlourFragment : Fragment() {
    private  var _binding : FragmentAllParlourBinding? = null
    private val binding get() = _binding!!
    private lateinit var nearbyAdapter : ParlourVerticalAdapter
    private lateinit var trendingAdapter : ParlourHorizontalAdapter
    private val locationViewModel : LocationViewModel by activityViewModels()
    private val parlourViewModel: ParlourViewModel by viewModels()
    private var lastLat: Double? = null
    private var lastLng: Double? = null
    private var lat: Double? = null
    private var lng: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllParlourBinding.inflate(inflater,container,false)
        return  binding.root
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
    }
    private fun observeLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.location.collect { state ->
                    when(state){
                        is LocationUiState.Idle -> {
                            binding.shimmerLayout.isVisible = false
                            binding.shimmerLayoutVerticalParlour.isVisible = false
                            binding.mainContent.isVisible = false
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
                                showNoInternetState(retryText = "Try Again"){
                                    if(!NetworkUtils.isInternetAvailable(requireContext())){
                                        Snackbar.make(
                                            binding.root,
                                            "No Internet Connection",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }else{
                                        binding.stateView.hide()
                                        if (lastLat != currentLat || lastLng != currentLng) {
                                            lastLat = currentLat
                                            lastLng = currentLng
                                            parlourViewModel.getNearbyParlours(type = "MENS")
                                            parlourViewModel.trendingParlours(type = "MENS")
                                        }else{
                                            hideMainLoading()
                                            binding.mainContent.isVisible = true

                                        }
                                    }
                                }
                            }
                            else {
                                if (lastLat != currentLat || lastLng != currentLng) {
                                    lastLat = currentLat
                                    lastLng = currentLng
                                    parlourViewModel.getNearbyParlours(type = "MENS")
                                    parlourViewModel.trendingParlours(type = "MENS")
                                }else{
                                    hideMainLoading()
                                    binding.mainContent.visibility = View.VISIBLE

                                }
                            }


                        }
                        is LocationUiState.Error -> {
                            binding.shimmerLayout.isVisible = false
                            hideMainLoading()
                            binding.mainContent.isVisible = false
                            showLocationError(
                                onRetry = {
                                    withInternet {
                                        binding.mainContent.isVisible = false
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
    private fun observeNearby() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parlourViewModel.nearbyParlourState.collect { state ->
                    when (state) {

                        is ParlourState.Loading -> {
                            binding.stateView.hide()
                            binding.mainContent.isVisible = false
                            binding.loaderNearby.isVisible = true
                            binding.shimmerLayout.isVisible = false
                        }

                        is ParlourState.Success -> {
                            binding.mainContent.isVisible = true
                            hideMainLoading()
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

                            binding.shimmerLayout.isVisible = false

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
    private fun observeTrending() {
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
                            Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
    private fun retryAllData() {

        showMainLoading()

        parlourViewModel.getNearbyParlours(
            type = "MENS",
            forceRefresh = true
        )

        parlourViewModel.trendingParlours(type = "MENS")


    }
    private fun setupRecyclerViews() {
        binding.rvNearbyParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        nearbyAdapter = ParlourVerticalAdapter ( onItemClick = ::openParlour)

        trendingAdapter = ParlourHorizontalAdapter ( onItemClick = ::openParlour)
        binding.rvNearbyParlours.adapter = nearbyAdapter
        binding.rvTrendingParlours.adapter = trendingAdapter
        binding.layoutTrendingServicesSection.isVisible = false
        binding.layoutBookingsSection.isVisible = false
    }
    private fun setupPagination(){
        binding.rvNearbyParlours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (dy > 0 && lastVisibleItem >= totalItemCount - 2) {

                    parlourViewModel.nearbyLoadNextPage("MENS")
                }

            }
        })
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
    private fun showNearbyError(
        onRetry: () -> Unit,
        onChangeLocation: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "Unable to load parlours 😔",
                subtitle = "Something went wrong while loading nearby parlours.\nPlease retry or change your location",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry,
                secondaryButtonText = "Change Location",
                onSecondaryClick = onChangeLocation
            )
        )
    }
    private fun showMainLoading() {
        binding.shimmerLayoutVerticalParlour.isVisible = true
        binding.shimmerLayoutVerticalParlour.startShimmer()
        binding.mainContent.isVisible = false
    }
    private fun hideMainLoading() {
        binding.shimmerLayoutVerticalParlour.stopShimmer()
        binding.shimmerLayoutVerticalParlour.isVisible = false
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}