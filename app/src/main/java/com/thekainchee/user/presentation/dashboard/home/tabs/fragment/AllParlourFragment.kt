package com.thekainchee.user.presentation.dashboard.home.tabs.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.thekainchee.user.presentation.dashboard.home.adapter.ParlourHorizontalAdapter
import com.thekainchee.user.presentation.dashboard.home.adapter.TrendingServiceAdapter
import com.thekainchee.user.presentation.dashboard.home.adapter.UpcomingBookingAdapter
import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI
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
import kotlin.getValue
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
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.mainContent.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }
        binding.rvNearbyParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendingServices.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingBookings.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutFullEmpty.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                hideFullEmpty()
                binding.mainContent.isVisible = false
                retryAllData()
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
                binding.layoutNoInternet.visibility = View.GONE
                if(lat != null && lng != null){
                    retryAllData()
                }else{
                    binding.layoutNoInternet.visibility = View.GONE
                    locationViewModel.fetchUserLocation()
                }
            }
        }

        binding.btnChangeLocation.setOnClickListener {
            val intent = Intent(requireContext(), LocationActivity::class.java)
            startActivity(intent)
        }
        nearbyAdapter = ParlourHorizontalAdapter ( onItemClick = { item ->

            val intent = Intent(requireContext(), ParlourActivity::class.java)
            intent.putExtra("parlourId", item.id)
            intent.putExtra("distance", item.distance.toString())
            startActivity(intent)
        })

        trendingAdapter = ParlourHorizontalAdapter ( onItemClick = { item ->
            val intent = Intent(requireContext(), ParlourActivity::class.java)
            intent.putExtra("parlourId", item.id)
            intent.putExtra("distance", item.distance.toString())
            startActivity(intent)
        })

        trendingServiceAdapter = TrendingServiceAdapter(onItemClick = {

        })

        upcomingBookingAdapter = UpcomingBookingAdapter(onItemClick = {

        })
        val dummyServices = listOf(
            ServiceUI("Hair Cut", 120, 150.0, 30.0,"https://cdn.pixabay.com/photo/2020/05/14/12/37/barber-5194406_1280.jpg"),
            ServiceUI("Facial", 80, 500.0, 45.0,"https://tse4.mm.bing.net/th/id/OIP.mqlq8vlalAPSMnzXT-ga4wHaE8?pid=Api&h=220&P=0"),
            ServiceUI("Manicure", 60, 300.0, 25.0,"https://tse4.mm.bing.net/th/id/OIP.O2o1h41k64h5D7o7464edwHaE8?pid=Api&h=220&P=0"),
            ServiceUI("Pedicure", 55, 350.0, 35.0,"https://tse1.mm.bing.net/th/id/OIP.oVqQovLKSuXUwsUwhBjC4wHaE8?pid=Api&h=220&P=0"),
            ServiceUI("Beard Trim", 90, 100.0, 15.0,"https://tse2.mm.bing.net/th/id/OIP.ue80bFueozMqdG3R_RdGYAHaEv?pid=Api&h=220&P=0"),
            ServiceUI("Hair Spa", 70, 800.0, 60.0,"https://zanya.co.in/wp-content/uploads/2024/02/woman-getting-hair-treatment.jpg"),
            ServiceUI("Cleanup", 40, 250.0, 20.0,"https://5.imimg.com/data5/FP/GJ/HR/SELLER-110510653/facial-and-skin-treatment-500x500.jpg"),
            ServiceUI("Massage", 30, 1200.0, 90.0,"https://tse4.mm.bing.net/th/id/OIP.5InsvmiQnyNATGp8Ho-ErQHaE8?pid=Api&h=220&P=0")
        )
        val dummyParlours = listOf(
            ParlourUI(
                "1",
                "Royal Salon",
                "https://img.lovepik.com/bg/20231224/Hair-Salon-Design-with-High-Definition-Elevating-Your-Salon-Service_2487132_wh1200.png",
                4.5,
                1.2,
                "UNISEX"
            ),
            ParlourUI("2", "Style Hub", "https://i.pinimg.com/originals/1b/80/23/1b80234280c4c55a76cce8548667347f.jpg", 4.2, 2.0, "MENS"),
            ParlourUI("3", "Beauty Zone", "https://www.architectandinteriorsindia.com/cloud/2023/04/28/DSC07381-scaled.jpg", 4.7, 0.8, "BEAUTY"),
            ParlourUI("4", "Urban Cuts", "https://i.pinimg.com/originals/51/eb/e7/51ebe75484b70de648bb28746d224d51.jpg", 4.3, 1.5, "UNISEX"),
            ParlourUI("5", "Gentlemen Groom", "https://i.pinimg.com/originals/7c/bf/70/7cbf70aaffe5d2ac986d42a2665856bb.jpg", 4.1, 2.3, "MENS")
        )
        val dummyBookings = listOf(
            BookingUI(
                id = "1",
                serviceSummary = "Haircut + 2 more",
                totalPrice = 299,
                totalDurationMinutes = 60,
                bookingDate = "2026-04-19", // Today
                slotStartTime = "05:30 PM",
                slotEndTime = "06:30 PM",
                bookingStatus = "CONFIRMED",
                image = "https://cdn.pixabay.com/photo/2020/05/14/12/37/barber-5194406_1280.jpg"
            ),
            BookingUI(
                id = "2",
                serviceSummary = "Beard Trim",
                totalPrice = 120,
                totalDurationMinutes = 20,
                bookingDate = "2026-04-20", // Tomorrow
                slotStartTime = "11:00 AM",
                slotEndTime = "11:20 AM",
                bookingStatus = "PENDING",
                image = "https://tse4.mm.bing.net/th/id/OIP.mqlq8vlalAPSMnzXT-ga4wHaE8?pid=Api&h=220&P=0"
            ),
            BookingUI(
                id = "3",
                serviceSummary = "Facial + 1 more",
                totalPrice = 499,
                totalDurationMinutes = 90,
                bookingDate = "2026-04-25",
                slotStartTime = "02:00 PM",
                slotEndTime = "03:30 PM",
                bookingStatus = "CONFIRMED",
                image = "https://tse4.mm.bing.net/th/id/OIP.O2o1h41k64h5D7o7464edwHaE8?pid=Api&h=220&P=0"
            ),
            BookingUI(
                id = "4",
                serviceSummary = "Hair Spa",
                totalPrice = 699,
                totalDurationMinutes = 75,
                bookingDate = "2026-05-01",
                slotStartTime = "04:00 PM",
                slotEndTime = "05:15 PM",
                bookingStatus = "PENDING",
                image  = "https://tse1.mm.bing.net/th/id/OIP.oVqQovLKSuXUwsUwhBjC4wHaE8?pid=Api&h=220&P=0"
            )
        )
        binding.rvNearbyParlours.adapter = nearbyAdapter
        binding.rvTrendingParlours.adapter = trendingAdapter
        binding.rvTrendingServices.adapter = trendingServiceAdapter
        binding.rvUpcomingBookings.adapter = upcomingBookingAdapter
        nearbyAdapter.submitList(dummyParlours)
        trendingAdapter.submitList(dummyParlours)
        upcomingBookingAdapter.submitList(dummyBookings)
        trendingServiceAdapter.submitList(dummyServices)
        observeUiStates()



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
    private fun observeUiStates(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch{
                    locationViewModel.location.collect { state ->


                        when(state){
                            is LocationUiState.Idle -> {
                                binding.shimmerLayout.isVisible = false
                                binding.mainContent.isVisible = false
                                binding.layoutFullEmpty.isVisible = false
                            }
                            is LocationUiState.Loading -> {
                                binding.shimmerLayout.isVisible = true
                                binding.shimmerLayout.startShimmer()
                                binding.mainContent.isVisible = false
                                binding.layoutFullEmpty.isVisible = false
                            }
                            is LocationUiState.Success -> {
                                val currentLat = state.address.latitude
                                val currentLng = state.address.longitude

                                lat = currentLat
                                lng = currentLng

                                parlourViewModel.setLocation(currentLat, currentLng)

                                if(!NetworkUtils.isInternetAvailable(requireContext())){
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.layoutNoInternet.isVisible = true
                                    return@collect
                                }else{


                                    if (lastLat != currentLat || lastLng != currentLng) {

                                        lastLat = currentLat
                                        lastLng = currentLng


                                        parlourViewModel.getNearbyParlours(type = null)
                                        parlourViewModel.trendingParlours(type = null)
//                                    parlourViewModel.trendingServices()
//                                    parlourViewModel.upcomingBookings()
                                    }else{
                                        binding.shimmerLayout.stopShimmer()
                                        binding.shimmerLayout.visibility = View.GONE
                                        binding.mainContent.visibility = View.VISIBLE
                                        Toast.makeText(
                                            requireContext(),
                                            "Using current location",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }
                            is LocationUiState.Error -> {
                                binding.shimmerLayout.isVisible = false
                                binding.shimmerLayout.stopShimmer()
                                binding.mainContent.isVisible = false
                                binding.layoutFullEmpty.isVisible = true
                                binding.btnRetry.isVisible = false
                                binding.btnChangeLocation.isVisible = false
                                binding.tvEmptyTitle.text = "Location unavailable 📍"
                                binding.imgEmpty.setImageResource(R.drawable.img_loc)
                                binding.tvEmptySubtitle.text =
                                    "We couldn't access your location.\n" +
                                            "Please retry from the top location bar."
                            }


                        }
                    }
                }

                launch{
                    parlourViewModel.nearbyParlourState.collect { state ->

                        when (state) {

                            is ParlourState.Loading -> {

                                binding.loaderNearby.isVisible = true
                            }

                            is ParlourState.Success -> {
                                binding.mainContent.isVisible = true

                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.isVisible = false

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

                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.isVisible = false
                                binding.loaderNearby.isVisible = false
                                binding.layoutNearbySection.isVisible = false
                                binding.mainContent.isVisible = false
                                binding.layoutFullEmpty.isVisible = true
                                binding.btnRetry.isVisible = true
                                binding.btnChangeLocation.isVisible = true
                                binding.tvEmptyTitle.text = "Unable to load parlours \uD83D\uDE14"
                                binding.imgEmpty.setImageResource(R.drawable.ic_oops)
                                binding.tvEmptySubtitle.text =
                                    "Something went wrong while loading nearby parlours.\nPlease retry or change your location."

                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    parlourViewModel.trendingParlourState.collect {state ->
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

                launch {
                    parlourViewModel.trendingServiceState.collect {state->
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
                                Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()

                            }else -> Unit
                        }
                    }
                }

                launch {
                    parlourViewModel.bookingState.collect { state->
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
                                Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()

                            }else -> Unit
                        }
                    }
                }
            }
        }
    }
    private fun retryAllData() {

        binding.shimmerLayout.isVisible = true
        binding.shimmerLayout.startShimmer()

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
        binding.layoutFullEmpty.isVisible = true
    }
    private fun hideFullEmpty() {
        binding.mainContent.isVisible = true
        binding.layoutFullEmpty.isVisible = false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}