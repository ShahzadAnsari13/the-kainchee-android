package com.thekainchee.user.presentation.parlour.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.thekainchee.user.databinding.FragmentParlourDetailBinding
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.presentation.service.adapter.CategoryAdapter
import com.thekainchee.user.presentation.parlour.adapter.ImageSliderAdapter
import com.thekainchee.user.presentation.service.model.ServiceCategory
import com.thekainchee.user.presentation.parlour.state.ParlourDetailedState
import com.thekainchee.user.presentation.service.state.ServiceCategoryState
import com.thekainchee.user.presentation.parlour.viewModel.ParlourDetailViewModel
import com.thekainchee.user.presentation.service.viewModel.ServiceViewModel
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
    private val parlourDetailedViewModel : ParlourDetailViewModel by viewModels()

    private val serviceViewModel : ServiceViewModel by viewModels()
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
        id?.let {
            parlourDetailedViewModel.getParlourDetails(it)
            serviceViewModel.getServiceCategories(it)
        }
        binding.btnRetry.setOnClickListener {
            id?.let {
                parlourDetailedViewModel.getParlourDetails(it)
                serviceViewModel.getServiceCategories(it)
            }
        }
        binding.tvRating.setOnClickListener {
            latitude?.let {lat ->
                longitude?.let {lng ->
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

                                binding.shimmerLayout.startShimmer()
                            }
                            is ParlourDetailedState.Success -> {
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE

                                binding.mainContent.visibility = View.VISIBLE
                                binding.errorLayout.visibility = View.GONE
                                val data = state.data
                                latitude = data.location.latitude.toString()
                                longitude = data.location.longitude.toString()

                                (requireActivity() as ParlourActivity)
                                    .setToolbarTitle(data.name ?: "Parlour Details")
                                binding.tvName.text = data.name
                                val safeDistance = distance ?: "--"
                                binding.tvRating.text = "⭐ ${"%.1f".format(data.ratingAverage)} (${data.ratingCount}) • 📍 $safeDistance km"
                                binding.tvDescription.text = if (!data.description.isNullOrBlank()) {
                                    data.description
                                } else {
                                    "${data.type} Parlour • ${data.workersCount} professionals • Hygienic services • Open ${data.openTime} - ${data.closeTime}"
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
                                binding.shimmerCategory.startShimmer()
                            }
                            is ServiceCategoryState.Success ->{
                                binding.shimmerCategory.visibility = View.GONE
                                binding.rvCategories.visibility = View.VISIBLE
                                binding.emptyLayout.visibility = View.GONE
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
                                binding.emptyLayout.visibility = View.VISIBLE
                                binding.shimmerCategory.stopShimmer()
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


        serviceCategoryAdapter = CategoryAdapter(categories) { category ->
            val parlourId = id ?: return@CategoryAdapter
            val action =
                ParlourDetailFragmentDirections.actionParlourDetailedFragmentToServiceListFragment(
                    parlourId = parlourId,
                    categoryId = category.id,
                    categoryName = category.name
                )
            findNavController().navigate(action)
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

    override fun onDestroyView() {
        super.onDestroyView()

        autoScrollJob?.cancel()

        binding.viewPagerImages.unregisterOnPageChangeCallback(pageCallback)

        _binding = null
    }
}