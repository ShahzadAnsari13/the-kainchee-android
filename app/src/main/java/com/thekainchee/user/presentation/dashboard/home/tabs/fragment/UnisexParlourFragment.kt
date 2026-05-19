package com.thekainchee.user.presentation.dashboard.home.tabs.fragment

import android.content.Intent
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
import kotlin.getValue

@AndroidEntryPoint
class UnisexParlourFragment : Fragment() {
    private var _binding : FragmentAllParlourBinding? = null
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.mainContent.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }
        binding.rvNearbyParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.layoutTrendingServicesSection.isVisible = false
        binding.layoutBookingsSection.isVisible = false
//        val dummyParlours = listOf(
//            ParlourUI(
//                id = "1",
//                name = "Style Studio",
//                image = "https://images.unsplash.com/photo-1600948836101-f9ffda59d250",
//                rating = 4.2,
//                distance = 1.2,
//                type = "UNISEX"
//            ),
//            ParlourUI(
//                id = "2",
//                name = "Urban Cuts",
//                image = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70",
//                rating = 4.3,
//                distance = 3.5,
//                type = "UNISEX"
//            ),
//            ParlourUI(
//                id = "3",
//                name = "Trendy Touch Salon",
//                image = "https://images.unsplash.com/photo-1560066984-138dadb4c035",
//                rating = 4.6,
//                distance = 0.9,
//                type = "UNISEX"
//            ),
//            ParlourUI(
//                id = "4",
//                name = "Mirror Magic Studio",
//                image = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9",
//                rating = 4.5,
//                distance = 1.8,
//                type = "UNISEX"
//            ),
//            ParlourUI(
//                id = "5",
//                name = "Glow & Style Hub",
//                image = "https://images.unsplash.com/photo-1559599101-f09722fb4948",
//                rating = 4.7,
//                distance = 2.4,
//                type = "UNISEX"
//            ),
//            ParlourUI(
//                id = "6",
//                name = "Perfect Look Studio",
//                image = "https://images.unsplash.com/photo-1512496015851-a90fb38ba796",
//                rating = 4.4,
//                distance = 1.6,
//                type = "UNISEX"
//            )
//        )
        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutFullEmpty.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else {

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
        nearbyAdapter = ParlourVerticalAdapter ( onItemClick = { item ->
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
        binding.rvNearbyParlours.adapter = nearbyAdapter
        binding.rvTrendingParlours.adapter = trendingAdapter
//        nearbyAdapter.submitList(dummyParlours)
//        trendingAdapter.submitList(dummyParlours)
        observeUiStates()
        binding.rvNearbyParlours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (dy > 0 && lastVisibleItem >= totalItemCount - 2) {

                    parlourViewModel.nearbyLoadNextPage("UNISEX")
                }

            }
        })


    }
    private fun observeUiStates(){

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch{
                    locationViewModel.location.collect { state ->


                        when(state){
                            is LocationUiState.Idle -> {
                                binding.shimmerLayout.isVisible = false
                                binding.shimmerLayoutVerticalParlour.isVisible = false
                                binding.mainContent.isVisible = false
                                binding.layoutFullEmpty.isVisible = false
                            }
                            is LocationUiState.Loading -> {
                                binding.shimmerLayout.isVisible = false
                                binding.shimmerLayoutVerticalParlour.isVisible = true
                                binding.shimmerLayoutVerticalParlour.startShimmer()
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
                                    binding.shimmerLayoutVerticalParlour.stopShimmer()
                                    binding.shimmerLayoutVerticalParlour.visibility = View.GONE
                                    binding.layoutNoInternet.visibility = View.VISIBLE
                                }
                                else {

                                    if (lastLat != currentLat || lastLng != currentLng) {

                                        lastLat = currentLat
                                        lastLng = currentLng



                                        parlourViewModel.getNearbyParlours(type = "UNISEX")
                                        parlourViewModel.trendingParlours(type = "UNISEX")
                                    }else{
                                        binding.shimmerLayoutVerticalParlour.stopShimmer()
                                        binding.shimmerLayoutVerticalParlour.visibility = View.GONE
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

                                binding.shimmerLayoutVerticalParlour.stopShimmer()
                                binding.shimmerLayoutVerticalParlour.isVisible = false
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
                                binding.shimmerLayout.isVisible = false

                                binding.shimmerLayoutVerticalParlour.stopShimmer()
                                binding.shimmerLayoutVerticalParlour.isVisible = false
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

                                binding.shimmerLayoutVerticalParlour.stopShimmer()
                                binding.shimmerLayoutVerticalParlour.isVisible = false
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
                               }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
    private fun retryAllData() {

        binding.shimmerLayoutVerticalParlour.isVisible = true
        binding.shimmerLayoutVerticalParlour.startShimmer()

        parlourViewModel.getNearbyParlours(
            type = "UNISEX",
            forceRefresh = true
        )

        parlourViewModel.trendingParlours(type = "UNISEX")


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