package com.thekainchee.user.presentation.dashboard.home.tabs.fragment

import android.content.Intent
import android.os.Bundle
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
import com.thekainchee.user.databinding.FragmentAllParlourBinding
import com.thekainchee.user.presentation.dashboard.home.adapter.ParlourHorizontalAdapter
import com.thekainchee.user.presentation.dashboard.home.adapter.TrendingServiceAdapter
import com.thekainchee.user.presentation.dashboard.home.adapter.UpcomingBookingAdapter
import com.thekainchee.user.presentation.dashboard.home.model.BookingUI
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.model.ServiceUI
import com.thekainchee.user.presentation.dashboard.home.state.BookingState
import com.thekainchee.user.presentation.dashboard.home.state.ParlourState
import com.thekainchee.user.presentation.dashboard.home.state.TrendingServiceState
import com.thekainchee.user.presentation.dashboard.home.viewModel.LocationViewModel
import com.thekainchee.user.presentation.dashboard.home.viewModel.ParlourViewModel
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.presentation.parlour.ParlourActivity
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
    private  val viewModel : LocationViewModel by activityViewModels()
    private val parlourViewModel : ParlourViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllParlourBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNearbyParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrendingServices.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingBookings.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.btnRetry.setOnClickListener {
            hideFullEmpty()
            parlourViewModel.getNearbyParlours(type = null, forceRefresh = true)
            parlourViewModel.trendingParlours(type = null)
            parlourViewModel.trendingServices()
            parlourViewModel.upcomingBookings()
        }

        binding.btnChangeLocation.setOnClickListener {
            val intent = Intent(requireContext(), LocationActivity::class.java)
            startActivity(intent)
        }
        nearbyAdapter = ParlourHorizontalAdapter ( onItemClick = { item ->
            val intent = Intent(context, ParlourActivity::class.java)
            intent.putExtra("parlourId", item.id)
            intent.putExtra("distance", item.distance)
            startActivity(intent)
        })

        trendingAdapter = ParlourHorizontalAdapter ( onItemClick = { item ->
            val intent = Intent(context, ParlourActivity::class.java)
            intent.putExtra("parlourId", item.id)
            intent.putExtra("distance", item.distance)
            startActivity(intent)
        })

        trendingServiceAdapter = TrendingServiceAdapter(onItemClick = {

        })

        upcomingBookingAdapter = UpcomingBookingAdapter(onItemClick = {

        })
        val dummyServices = listOf(
            ServiceUI("Hair Cut", 120, 150.0, 30.0),
            ServiceUI("Facial", 80, 500.0, 45.0),
            ServiceUI("Manicure", 60, 300.0, 25.0),
            ServiceUI("Pedicure", 55, 350.0, 35.0),
            ServiceUI("Beard Trim", 90, 100.0, 15.0),
            ServiceUI("Hair Spa", 70, 800.0, 60.0),
            ServiceUI("Cleanup", 40, 250.0, 20.0),
            ServiceUI("Massage", 30, 1200.0, 90.0)
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
                bookingStatus = "CONFIRMED"
            ),
            BookingUI(
                id = "2",
                serviceSummary = "Beard Trim",
                totalPrice = 120,
                totalDurationMinutes = 20,
                bookingDate = "2026-04-20", // Tomorrow
                slotStartTime = "11:00 AM",
                slotEndTime = "11:20 AM",
                bookingStatus = "PENDING"
            ),
            BookingUI(
                id = "3",
                serviceSummary = "Facial + 1 more",
                totalPrice = 499,
                totalDurationMinutes = 90,
                bookingDate = "2026-04-25",
                slotStartTime = "02:00 PM",
                slotEndTime = "03:30 PM",
                bookingStatus = "CONFIRMED"
            ),
            BookingUI(
                id = "4",
                serviceSummary = "Hair Spa",
                totalPrice = 699,
                totalDurationMinutes = 75,
                bookingDate = "2026-05-01",
                slotStartTime = "04:00 PM",
                slotEndTime = "05:15 PM",
                bookingStatus = "PENDING"
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch{
                    viewModel.location.collect { address ->
                        address?.let {

                            parlourViewModel.setLocation(it.latitude, it.longitude)
                            parlourViewModel.getNearbyParlours(type = null)
                            parlourViewModel.trendingParlours(type = null)
                            parlourViewModel.trendingServices()
                            parlourViewModel.upcomingBookings()
                        }
                    }
                }

                launch{
                    parlourViewModel.nearbyParlourState.collect { state ->

                        when (state) {

                            is ParlourState.Loading -> {
                                binding.tvNearbyViewAll.isInvisible = true
                                binding.tvNearbyViewAll.isClickable = false
                                binding.loaderNearby.isVisible = true
                            }

                            is ParlourState.Success -> {

                                binding.tvNearbyViewAll.isVisible = true
                                binding.tvNearbyViewAll.isClickable = true
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

                                binding.tvNearbyViewAll.isVisible = true
                                binding.tvNearbyViewAll.isClickable = true
                                binding.loaderNearby.isVisible = false

                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    parlourViewModel.trendingParlourState.collect {state ->
                        when (state){
                            is ParlourState.Loading -> {
                                binding.tvTrendingPViewAll.isInvisible = true
                                binding.tvTrendingPViewAll.isClickable = false
                                binding.loaderTrendingParlour.isVisible = true
                            }
                            is ParlourState.Success ->{
                                //hideLoader()
                                binding.tvTrendingPViewAll.isVisible = true
                                binding.tvTrendingPViewAll.isClickable = true
                                binding.loaderTrendingParlour.isVisible = false
                                binding.layoutTrendingParloursSection.isVisible = state.data.isNotEmpty()
                                trendingAdapter.submitList(state.data)
                            }
                            is ParlourState.Error->{
                                binding.tvTrendingPViewAll.isVisible = true
                                binding.tvTrendingPViewAll.isClickable = true
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
                                binding.tvTrendingSViewAll.isInvisible = true
                                binding.tvTrendingSViewAll.isClickable = false
                                binding.loaderTrendingService.isVisible = true

                            }
                            is TrendingServiceState.Success -> {

                                binding.tvTrendingSViewAll.isVisible = true
                                binding.tvTrendingSViewAll.isClickable = true
                                binding.loaderTrendingService.isVisible = false
                                binding.layoutTrendingServicesSection.isVisible = state.data.isNotEmpty()
                                trendingServiceAdapter.submitList(state.data)
                            }
                            is TrendingServiceState.Error -> {
                                binding.tvTrendingSViewAll.isVisible = true
                                binding.tvTrendingSViewAll.isClickable = true
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
                                binding.tvUpcomingBookingsViewAll.isInvisible = true
                                binding.tvUpcomingBookingsViewAll.isClickable = false
                                binding.loaderUpcomingBooking.isVisible = true
                            }
                            is BookingState.Success -> {

                                binding.tvUpcomingBookingsViewAll.isVisible = true
                                binding.tvUpcomingBookingsViewAll.isClickable = true
                                binding.loaderUpcomingBooking.isVisible = false
                                binding.layoutBookingsSection.isVisible = state.data.isNotEmpty()
                                upcomingBookingAdapter.submitList(state.data)
                            }
                            is BookingState.Error -> {

                                binding.tvUpcomingBookingsViewAll.isVisible = true
                                binding.tvUpcomingBookingsViewAll.isClickable = true
                                binding.loaderUpcomingBooking.isVisible = false
                                Toast.makeText(requireContext(),state.message, Toast.LENGTH_SHORT).show()

                            }else -> Unit
                        }
                    }
                }
            }
        }


        binding.rvNearbyParlours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem >= totalItemCount - 2) {

                    parlourViewModel.nearbyLoadNextPage(type = null)
                }

            }
        })

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