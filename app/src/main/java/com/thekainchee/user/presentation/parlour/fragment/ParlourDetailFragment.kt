package com.thekainchee.user.presentation.parlour.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentParlourDetailBinding
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.presentation.service.adapter.CategoryAdapter
import com.thekainchee.user.presentation.parlour.adapter.ImageSliderAdapter
import com.thekainchee.user.presentation.parlour.model.ParlourDetailedUI
import com.thekainchee.user.presentation.service.model.ServiceCategory
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.parlour.state.ParlourEvent
import com.thekainchee.user.presentation.service.state.ServiceCategoryState
import com.thekainchee.user.presentation.parlour.viewModel.ParlourDetailViewModel
import com.thekainchee.user.presentation.service.bottomSheet.BookingPreviewBottomSheet
import com.thekainchee.user.presentation.service.state.BookingPreviewEvent
import com.thekainchee.user.presentation.service.viewModel.ServiceViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
@AndroidEntryPoint
class ParlourDetailFragment : Fragment() {

    private var _binding: FragmentParlourDetailBinding? = null
    private val binding get() = _binding!!

    private var autoScrollJob: Job? = null

    private lateinit var serviceCategoryAdapter: CategoryAdapter
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    private lateinit var pageCallback: ViewPager2.OnPageChangeCallback
    private val args: ParlourDetailFragmentArgs by navArgs()
    private var id: String? = null
    private var distance: String? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var isOpened = false
    private val parlourDetailedViewModel : ParlourDetailViewModel by viewModels()
    private var selectedCategory: ServiceCategory? = null
    private val serviceViewModel : ServiceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParlourDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = args.parlourId
        distance = args.distance
        setupViewPagerCallback()
        setupClickListeners()
        observeParlourDetails()
        checkInternetAndLoad()
    }

    private fun observeParlourDetails() {
        observeParlourState()
        observeServiceCategories()
        observeParlourEvents()
        observeSelectedServices()
        observeBookingPreview()
    }

    // CATEGORY GRID
    private fun setupCategories(categories: List<ServiceCategory>) {

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvCategories.setHasFixedSize(true)

        // TEMP DATA (API se replace karna baad me)



        serviceCategoryAdapter = CategoryAdapter(
            categories,
            ::onCategoryClick
        )

        binding.rvCategories.adapter = serviceCategoryAdapter
    }

    // VIEWPAGER CALLBACK (pause/resume)
    private fun setupViewPagerCallback() {

        pageCallback = object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        autoScrollJob?.cancel()
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {
                        startAutoScroll()
                    }
                }
            }
        }

        binding.viewPagerImages.registerOnPageChangeCallback(pageCallback)
    }

    // AUTO SCROLL
    private fun startAutoScroll() {

        autoScrollJob?.cancel()

        autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                delay(3000)

                val itemCount = binding.viewPagerImages.adapter?.itemCount ?: 0
                if (itemCount == 0) continue

                val nextItem =
                    (binding.viewPagerImages.currentItem + 1) % itemCount

                binding.viewPagerImages.currentItem = nextItem
            }
        }
    }


    private fun showBottomStrip() {

        binding.bottomBookingStrip.apply {

            if (visibility == View.VISIBLE) return

            visibility = View.VISIBLE
            alpha = 0f
            translationX = 300f

            animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(350)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }
    private fun hideBottomStrip() {

        binding.bottomBookingStrip.animate()
            .translationX(300f)
            .alpha(0f)
            .setDuration(250)
            .withEndAction {
                binding.bottomBookingStrip.visibility = View.GONE
            }
            .start()
    }
    private fun observeParlourState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parlourDetailedViewModel.parlourDetailedState.collect { state ->
                    when(state){
                        is ParlourDetailedState.Idle -> {

                        }
                        is ParlourDetailedState.Loading -> {
                            showMainLoading()
                        }
                        is ParlourDetailedState.Success -> {
                            hideMainLoading()
                            binding.mainContent.isVisible = true
                            val data = state.data
                            latitude = data.location.latitude.toString()
                            longitude = data.location.longitude.toString()
                            bindParlourDetails(data)
                            setupImageSlider(data.images)
                        }
                        is ParlourDetailedState.Error -> {
                            hideMainLoading()
                            binding.mainContent.isVisible = false
                            showParlourLoadError {
                                withInternet {
                                    id?.let { id ->
                                        parlourDetailedViewModel.getParlourDetails(id)
                                        serviceViewModel.getServiceCategories(id)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
    private fun observeServiceCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                serviceViewModel.serviceCategoryState.collect { state ->
                    when(state){
                        is ServiceCategoryState.Idle -> {

                        }
                        is ServiceCategoryState.Loading ->{
                            showCategoryLoading()
                        }
                        is ServiceCategoryState.Success ->{
                            hideCategoryLoading()
                            binding.rvCategories.isVisible = true
                            binding.emptyLayout.isVisible = false
                            setupCategories(state.data)
                        }
                        is ServiceCategoryState.Empty->{
                            hideCategoryLoading()
                            binding.rvCategories.isVisible = false
                            binding.emptyLayout.isVisible = true
                        }
                        is ServiceCategoryState.Error->{
                            hideCategoryLoading()
                            binding.rvCategories.isVisible = false
                            binding.emptyLayout.isVisible = true
                        }

                    }
                }
            }
        }
    }
    private fun observeParlourEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parlourDetailedViewModel.event.collect { event ->
                    when(event){
                        is ParlourEvent.NavigateToServices -> handleNavigateToServices()
                        is ParlourEvent.ShowError -> handleShowError(event.message)
                        is ParlourEvent.ParlourClosed -> handleParlourClosed()
                    }
                }
            }
        }
    }
    private fun observeSelectedServices() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                serviceViewModel.selectedServiceIds.collect { selectedIds ->
                    if(selectedIds.isNotEmpty()){

                        binding.tvSelectedCount.text =
                            "${selectedIds.size} Services Added"

                        showBottomStrip()

                    }else{

                        hideBottomStrip()
                    }
                }
            }
        }
    }
    private fun observeBookingPreview() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                serviceViewModel.bookingPreviewEvent.collect { event ->
                    when (event) {

                        is BookingPreviewEvent.OpenBottomSheet -> {
                            BookingPreviewBottomSheet
                                .newInstance(event.data,
                                    onChangesDone = {
                                        Snackbar.make(binding.root,"Booking preview updated", Snackbar.LENGTH_SHORT).show()
                                    })
                                .show(
                                    parentFragmentManager,
                                    "BookingPreviewBottomSheet"
                                )
                        }
                        is BookingPreviewEvent.ShowToast -> {
                            Snackbar.make(binding.root, event.message, Toast.LENGTH_SHORT).show()
                        }
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
    private fun showMainLoading() {
        binding.shimmerLayout.isVisible = true
        binding.shimmerLayout.startShimmer()
        binding.mainContent.isVisible = false
        binding.stateView.hide()
    }
    private fun hideMainLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.isVisible = false
    }
    private fun onCategoryClick(
        category: ServiceCategory,
        position: Int
    ) {
        val parlourId = id ?: return@onCategoryClick
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            Snackbar.make(
                binding.root,
                "No Internet Connection",
                Snackbar.LENGTH_SHORT
            ).show()
            return@onCategoryClick
        }else{
            if(isOpened){
                selectedCategory = category
                serviceCategoryAdapter.enableLoading(position)

                parlourDetailedViewModel.checkParlourStatus(parlourId)
            }else{
                Snackbar.make(
                    binding.root,
                    "Parlour is currently closed",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun bindParlourDetails(data: ParlourDetailedUI) {
        (requireActivity() as ParlourActivity)
            .setToolbarTitle(data.name ?: "Parlour Details")
        binding.tvName.text = data.name
        val safeDistance = distance ?: "--"
        binding.tvRatingAndMap.text = "⭐ ${"%.1f".format(data.ratingAverage)} (${data.ratingCount}) • 📍 $safeDistance km"
        binding.tvDescription.text = if (!data.description.isNullOrBlank()) {
            data.description
        } else {
            "${data.type} Parlour • ${data.workersCount} professionals • Hygienic services • Open ${data.openTime} - ${data.closeTime}"
        }
        isOpened = data.isOpenNow
        binding.laIsOpen.isVisible = isOpened
        binding.lottieClosedOverlay.isVisible = !isOpened
        if(isOpened){
            id?.let { id ->
                serviceViewModel.loadSelectedServices(id)
            }
        }
        binding.tvTiming.text = "🕒 ${data.openTime} - ${data.closeTime}"
        val days = listOf("SUN","MON","TUE","WED","THU","FRI","SAT")
        val today = days[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]
        binding.tvClosed.text = when {
            data.closeDay.contains(today) -> {
                "Closed Today"
            }
            data.closeDay.isNotEmpty() -> {
                val days = data.closeDay.joinToString(", ") {
                    it.lowercase().replaceFirstChar { c -> c.uppercase() }
                }
                "Closed • $days"
            }

            else -> {
                ""
            }
        }

        // location
        binding.tvAddress.text = "📍 ${listOfNotNull(data.location.details, data.location.landmark).joinToString(", ")}"
        binding.tvCityState.text =
            "${data.location.city}, ${data.location.state}"
        val points = buildList {
            add("${data.workersCount}+ Skilled Professionals")

            if (data.ratingAverage >= 4.0) {
                add("Highly Rated (${data.ratingAverage})")
            }


            add("Verified & Trusted")


            if (data.facilities.contains("AC")) {
                add("Comfortable AC Environment")
            }

            if (data.facilities.contains("Parking")) {
                add("Parking Available")
            }

            if (data.facilities.contains("WiFi")) {
                add("Free WiFi")
            }
        }.take(6)
        binding.tvFacilities.text =
            points.joinToString("\n") { "✨ $it" }
    }
    private fun setupImageSlider(images: List<String>) {
        imageSliderAdapter = ImageSliderAdapter(images)
        binding.viewPagerImages.adapter = imageSliderAdapter
        binding.dotsIndicator.setViewPager2(binding.viewPagerImages)
        startAutoScroll()
    }
    private fun handleNavigateToServices(){
        val category =
            selectedCategory ?: return@handleNavigateToServices

        val action =
            ParlourDetailFragmentDirections
                .actionParlourDetailedFragmentToServiceListFragment(
                    parlourId = id!!,
                    categoryId = category.id,
                    categoryName = category.name
                )

        findNavController().navigate(action)
        serviceCategoryAdapter.disableLoading()
    }

    private fun handleShowError(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        serviceCategoryAdapter.disableLoading()
    }

    private fun handleParlourClosed(){
        binding.lottieClosedOverlay.visibility = View.VISIBLE
        binding.laIsOpen.visibility = View.GONE
        isOpened = false
        Snackbar.make(
            binding.root,
            "Parlour is currently closed",
            Snackbar.LENGTH_SHORT
        ).show()
        serviceCategoryAdapter.disableLoading()
    }
    private fun showParlourLoadError(
        onRetry: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "Unable to Load Parlour",
                subtitle = "We couldn't load the parlour details right now. Please try again.",
                primaryButtonText = "Retry",
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
    private fun setupClickListeners(){
        binding.tvRatingAndMap.setOnClickListener {

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
                            ParlourDetailFragmentDirections
                                .actionParlourDetailedFragmentToParlourMap(
                                    latitude = lat,
                                    longitude = lng
                                )
                        findNavController().navigate(action)
                    }
                }
            }
        }
        binding.bottomBookingStrip.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                id?.let { parlourId ->
                    serviceViewModel.getBookingPreview(parlourId,serviceViewModel.selectedServiceIds.value)
                }

            }
        }
    }
    private fun checkInternetAndLoad(){
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
                    id?.let {
                        parlourDetailedViewModel.getParlourDetails(it)
                        serviceViewModel.getServiceCategories(it)
                    }
                }
            }
        }else{
            id?.let {
                parlourDetailedViewModel.getParlourDetails(it)
                serviceViewModel.getServiceCategories(it)
            }
        }
    }
    private fun showCategoryLoading(){
        binding.shimmerCategory.isVisible = true
        binding.shimmerCategory.startShimmer()
        binding.rvCategories.isVisible = false
        binding.emptyLayout.isVisible = false
    }
    private fun hideCategoryLoading(){
        binding.shimmerCategory.stopShimmer()
        binding.shimmerCategory.isVisible = false
    }
    override fun onDestroyView() {
        super.onDestroyView()

        autoScrollJob?.cancel()

        binding.viewPagerImages.unregisterOnPageChangeCallback(pageCallback)

        _binding = null
    }
}