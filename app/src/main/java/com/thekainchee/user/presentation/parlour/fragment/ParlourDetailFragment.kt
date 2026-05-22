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
import com.thekainchee.user.databinding.FragmentParlourDetailBinding
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.presentation.service.adapter.CategoryAdapter
import com.thekainchee.user.presentation.parlour.adapter.ImageSliderAdapter
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
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.mainContent.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }else{
            id?.let {
                parlourDetailedViewModel.getParlourDetails(it)
                serviceViewModel.getServiceCategories(it)
            }
        }

        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                id?.let {
                    parlourDetailedViewModel.getParlourDetails(it)
                    serviceViewModel.getServiceCategories(it)
                }
            }
        }

        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                id?.let {
                    parlourDetailedViewModel.getParlourDetails(it)
                    serviceViewModel.getServiceCategories(it)
                }
            }
        }
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
            Log.d("CLICK_TEST", "Clicked")
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                id?.let { parlourId ->
                    Log.d("parlourId",parlourId)
                    serviceViewModel.getBookingPreview(parlourId,serviceViewModel.selectedServiceIds.value)
                }

            }
        }
        observeParlourDetails()
    }

    private fun observeParlourDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch{
                    parlourDetailedViewModel.parlourDetailedState.collect{ state ->
                        when(state){
                            is ParlourDetailedState.Idle -> {

                            }
                            is ParlourDetailedState.Loading -> {
                                binding.shimmerLayout.visibility = View.VISIBLE
                                binding.mainContent.visibility = View.GONE
                                binding.errorLayout.visibility = View.GONE
                                binding.layoutNoInternet.visibility = View.GONE

                                binding.shimmerLayout.startShimmer()
                            }
                            is ParlourDetailedState.Success -> {
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE

                                binding.mainContent.visibility = View.VISIBLE
                                binding.errorLayout.visibility = View.GONE

                                binding.layoutNoInternet.visibility = View.GONE
                                val data = state.data
                                latitude = data.location.latitude.toString()
                                longitude = data.location.longitude.toString()

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

                                // slider update
                                imageSliderAdapter = ImageSliderAdapter(data.images)
                                binding.viewPagerImages.adapter = imageSliderAdapter
                                binding.dotsIndicator.setViewPager2(binding.viewPagerImages)
                                startAutoScroll()
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
                            is ParlourDetailedState.Error -> {
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE
                                binding.layoutNoInternet.visibility = View.GONE
                                binding.mainContent.visibility = View.GONE
                                binding.errorLayout.visibility = View.VISIBLE
                            }

                        }
                    }
                }
                launch {
                    serviceViewModel.serviceCategoryState.collect { state ->
                        when(state){
                            is ServiceCategoryState.Idle -> {

                            }
                            is ServiceCategoryState.Loading ->{
                                binding.shimmerCategory.visibility = View.VISIBLE
                                binding.rvCategories.visibility = View.GONE
                                binding.emptyLayout.visibility = View.GONE
                                binding.layoutNoInternet.visibility= View.GONE
                                binding.shimmerCategory.startShimmer()
                            }
                            is ServiceCategoryState.Success ->{
                                binding.shimmerCategory.visibility = View.GONE
                                binding.rvCategories.visibility = View.VISIBLE
                                binding.emptyLayout.visibility = View.GONE
                                binding.layoutNoInternet.visibility = View.GONE
                                binding.shimmerCategory.stopShimmer()
                                setupCategories(state.data)
                            }
                            is ServiceCategoryState.Empty->{
                                binding.shimmerCategory.visibility = View.GONE
                                binding.rvCategories.visibility = View.GONE
                                binding.emptyLayout.visibility = View.VISIBLE
                                binding.shimmerCategory.stopShimmer()
                            }
                            is ServiceCategoryState.Error->{
                                binding.shimmerCategory.visibility = View.GONE
                                binding.rvCategories.visibility = View.GONE
                                binding.layoutNoInternet.visibility= View.GONE
                                binding.emptyLayout.visibility = View.VISIBLE
                                binding.shimmerCategory.stopShimmer()
                            }

                        }
                    }
                }
                launch {
                    parlourDetailedViewModel.event.collect{
                        when(it){
                            is ParlourEvent.NavigateToServices -> {
                                val category =
                                    selectedCategory ?: return@collect

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

                            is ParlourEvent.ShowError -> {

                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                                serviceCategoryAdapter.disableLoading()
                            }
                            is ParlourEvent.ParlourClosed -> {

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
                        }
                    }
                }
                launch{
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
                launch {
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
    }

    // CATEGORY GRID
    private fun setupCategories(categories: List<ServiceCategory>) {

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvCategories.setHasFixedSize(true)

        // TEMP DATA (API se replace karna baad me)



        serviceCategoryAdapter = CategoryAdapter(categories) { category,position ->
            val parlourId = id ?: return@CategoryAdapter
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@CategoryAdapter
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
    override fun onDestroyView() {
        super.onDestroyView()

        autoScrollJob?.cancel()

        binding.viewPagerImages.unregisterOnPageChangeCallback(pageCallback)

        _binding = null
    }
}