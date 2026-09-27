package com.thekainchee.user.presentation.dashboard.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.thekainchee.user.databinding.FragmentHomeBinding
import com.thekainchee.user.presentation.common.bottomSheet.LocationPermissionBottomSheet
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.dashboard.adapter.ParlourHorizontalAdapter
import com.thekainchee.user.presentation.dashboard.adapter.PromotionAdapter
import com.thekainchee.user.presentation.dashboard.adapter.SalonCategoryAdapter
import com.thekainchee.user.presentation.dashboard.adapter.TrendingServiceAdapter
import com.thekainchee.user.presentation.dashboard.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.model.SalonCategory
import com.thekainchee.user.presentation.dashboard.viewModel.LocationViewModel
import com.thekainchee.user.presentation.dashboard.state.LocationUiState
import com.thekainchee.user.presentation.dashboard.state.ParlourState
import com.thekainchee.user.presentation.dashboard.state.TrendingServiceState
import com.thekainchee.user.presentation.dashboard.viewModel.ParlourViewModel
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.presentation.profile.ProfileActivity
import com.thekainchee.user.utils.LocationUtils
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private  val locationViewModel : LocationViewModel by activityViewModels()
    private var openedPermissionSettings = false
    private var shouldRefreshLocation = false
    private val parlourViewModel: ParlourViewModel by viewModels()
    private lateinit var nearbyAdapter: ParlourHorizontalAdapter
    private lateinit var trendingAdapter: ParlourHorizontalAdapter
    private lateinit var trendingServiceAdapter: TrendingServiceAdapter
    private lateinit var salonCategoryAdapter: SalonCategoryAdapter
    private var autoScrollJob: Job? = null
    private var lastLat: Double? = null
    private var lastLng: Double? = null
    private val promotionImages = listOf(
        R.drawable.promo_01_app_intro,
        R.drawable.promo_02_app_intro,
        R.drawable.promo_03_app_intro,
        R.drawable.promo_04_app_intro
    )
    private val salonCategories = listOf(
        SalonCategory(
            "Men Salon",
            R.drawable.ic_men_salon,
            SalonCategoryType.MENS
        ),
        SalonCategory(
            "Women Salon",
            R.drawable.ic_women_salon,
            SalonCategoryType.BEAUTY
        ),
        SalonCategory(
            "Unisex Salon",
            R.drawable.ic_unisex_salon,
            SalonCategoryType.UNISEX
        )
    )
    enum class SalonCategoryType {
        MENS,
        BEAUTY,
        UNISEX
    }
    private lateinit var selectedCategory: SalonCategoryType
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fine || coarse) {
                withInternet {
                    locationViewModel.fetchUserLocation()
                }
            } else {
                LocationPermissionBottomSheet {
                    openedPermissionSettings = true
                }.show(parentFragmentManager, "LocationPermissionBottomSheet")
            }
        }

    private val gpsResolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                LocationUtils.checkLocationPermission(
                    activity = requireActivity(),
                    launcher = locationPermissionLauncher
                ) {
                    withInternet {
                        locationViewModel.fetchUserLocation()
                    }
                }

            } else {
                requireActivity().finishAffinity()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGreeting.text = getGreeting()
        setupRecyclerViews()
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        binding.locationCard.setOnClickListener {
            startActivity(Intent(requireContext(), LocationActivity::class.java))
        }
        withInternet {
            LocationUtils.checkGpsStatus(
                requireActivity(),
                gpsResolutionLauncher
            ) {
                LocationUtils.checkLocationPermission(
                    activity = requireActivity(),
                    launcher = locationPermissionLauncher
                ) {
                    withInternet {
                        locationViewModel.fetchUserLocation()
                    }

                }
            }
        }
        observeUiStates()
        setupPagination()
        setupPromotionBanner()
        setupSalonCategories()
        binding.quickBookingCard.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Quick Booking Coming Soon",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupPromotionBanner() {
        val adapter = PromotionAdapter(promotionImages)

        binding.promotionViewPager.adapter = adapter

        binding.dotsIndicator.setViewPager2(
            binding.promotionViewPager
        )
        startAutoScroll()
    }
    private fun setupSalonCategories() {

        salonCategoryAdapter = SalonCategoryAdapter(
            categories = salonCategories
        ) { category ->

            withInternet {
                selectedCategory = category.type
                val type = selectedCategory.name
                parlourViewModel.getNearbyParlours(type)
                parlourViewModel.trendingParlours(type)
                parlourViewModel.trendingServices()
            }

        }

        binding.rvSalonCategories.apply {
            adapter = salonCategoryAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }
    private fun observeUiStates(){
        observeLocation()
        observeNearby()
        observeTrending()
        observeTrendingServices()
    }
    private fun retryAllData() {
        val type = selectedCategory.name
        parlourViewModel.getNearbyParlours(
            type,
            forceRefresh = true
        )
        parlourViewModel.trendingParlours(type)

        parlourViewModel.trendingServices()
    }

    private fun setupRecyclerViews() {

        binding.rvNearbyParlours.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvTrendingServices.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        nearbyAdapter = ParlourHorizontalAdapter(
            onItemClick = ::openParlour
        )

        trendingAdapter = ParlourHorizontalAdapter(
            onItemClick = ::openParlour
        )

        trendingServiceAdapter = TrendingServiceAdapter(
            onItemClick = {
                // service click later
            }
        )

        binding.rvNearbyParlours.adapter = nearbyAdapter
        binding.rvTrendingParlours.adapter = trendingAdapter
        binding.rvTrendingServices.adapter = trendingServiceAdapter
    }
    private fun openParlour(item: ParlourUI) {
        startActivity(
            Intent(requireContext(), ParlourActivity::class.java).apply {
                putExtra("parlourId", item.id)
                putExtra("distance", item.distance.toString())
            }
        )
    }
    private fun observeLocation(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                locationViewModel.location.collect { state ->
                    when(state){
                        is LocationUiState.Idle ->{
                        }
                        is LocationUiState.Loading -> {
                            binding.tvLocation.text = "Fetching location..."
                            showMainLoading()
                        }

                        is LocationUiState.Success -> {
                            binding.tvLocation.text = state.address.city
                            val currentLat = state.address.latitude
                            val currentLng = state.address.longitude
                            parlourViewModel.setLocation(currentLat, currentLng)

                            withInternet {
                                if (lastLat != currentLat || lastLng != currentLng) {
                                    lastLat = currentLat
                                    lastLng = currentLng
                                    selectedCategory = SalonCategoryType.MENS
                                    val type = selectedCategory.name
                                    parlourViewModel.getNearbyParlours(type)
                                    parlourViewModel.trendingParlours(type)
                                    parlourViewModel.trendingServices()

                                }else{
                                    binding.stateView.hide()
                                }
                            }

                            askNotificationPermission()
                        }

                        is LocationUiState.Error -> {

                            val text = "\uD83D\uDCCD Unable to load location  Retry"

                            val spannable = SpannableString(text)

                            val retryStart = text.indexOf("Retry")
                            val retryEnd = retryStart + "Retry".length
                            spannable.setSpan(
                                object : ClickableSpan() {
                                    override fun onClick(widget: View) {
                                        locationViewModel.fetchUserLocation()
                                    }
                                    override fun updateDrawState(ds: TextPaint) {
                                        super.updateDrawState(ds)
                                        ds.isUnderlineText = false
                                        ds.isFakeBoldText = true
                                        ds.color = ContextCompat.getColor(
                                            requireContext(),
                                            R.color.primaryColor
                                        )
                                    }
                                },
                                retryStart,
                                retryEnd,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE


                            )

                            setLocationText(spannable)
                            hideMainLoading()
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
    private fun observeNearby() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                parlourViewModel.nearbyParlourState.collect { state ->

                    when (state) {

                        is ParlourState.Loading -> {
                            showMainLoading()
                        }

                        is ParlourState.Success -> {
                            hideMainLoading()
                            binding.mainContent.isVisible = true
                            binding.nearbyParloursSection.isVisible = state.data.isNotEmpty()
                            if (state.data.isEmpty()) {
                                showFullEmpty()
                            }else{
                                nearbyAdapter.submitList(state.data)
                            }
                        }

                        is ParlourState.Error -> {
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

                    when (state) {

                        is ParlourState.Loading -> {

                        }

                        is ParlourState.Success -> {
                            binding.trendingParloursSection.isVisible =
                                state.data.isNotEmpty()
                            trendingAdapter.submitList(state.data)
                        }

                        is ParlourState.Error -> {
                            binding.trendingParloursSection.isVisible = false
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeTrendingServices() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                parlourViewModel.trendingServiceState.collect { state ->

                    when (state) {

                        is TrendingServiceState.Loading -> {
                            // Abhi loader nahi lagayenge
                        }

                        is TrendingServiceState.Success -> {
                            binding.trendingServicesSection.isVisible =
                                state.data.isNotEmpty()
                            trendingServiceAdapter.submitList(state.data)
                        }

                        is TrendingServiceState.Error -> {
                            binding.trendingServicesSection.isVisible = false
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupPagination(){
        binding.rvNearbyParlours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (dx >0 && lastVisibleItem >= totalItemCount - 2) {
                    val type = selectedCategory.name
                    parlourViewModel.nearbyLoadNextPage(type)
                }
            }
        })
    }
    private fun getGreeting():String{
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good Morning ☕"
            in 12..16 -> "Good Afternoon 🌤"
            else -> "Good Evening ✨"
        }
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

    override fun onResume() {
        super.onResume()
        withInternet {
            if (openedPermissionSettings) {

                openedPermissionSettings = false

                LocationUtils.checkLocationPermission(
                    activity = requireActivity(),
                    launcher = locationPermissionLauncher
                ) {
                    withInternet {
                        locationViewModel.fetchUserLocation()
                    }
                }
            }else if (shouldRefreshLocation) {

                shouldRefreshLocation = false
                withInternet {
                    locationViewModel.fetchUserLocation()
                }
            }
        }


    }

    private fun setLocationText(spannable: SpannableString) {
        binding.tvLocation.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = spannable
        }
    }
    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }

    private fun showNoInternetState(
        retryText: String = "Try Again",
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
    private fun startAutoScroll() {
        autoScrollJob?.cancel()

        autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(3000)

                val nextItem = binding.promotionViewPager.currentItem + 1

                if (nextItem < binding.promotionViewPager.adapter?.itemCount ?: 0) {
                    binding.promotionViewPager.setCurrentItem(nextItem, true)
                } else {
                    binding.promotionViewPager.setCurrentItem(0, true)
                }
            }
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
                title = "Unable to load parlours",
                subtitle = "Something went wrong while loading nearby parlours.\nPlease retry or change your location",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry,
                secondaryButtonText = "Change Location",
                onSecondaryClick = onChangeLocation
            )
        )
    }
    private fun openLocationScreen() {
        startActivity(
            Intent(requireContext(), LocationActivity::class.java)
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollJob?.cancel()
        autoScrollJob = null
        _binding = null
    }



}