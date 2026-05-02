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
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentAllParlourBinding
import com.thekainchee.user.presentation.dashboard.home.adapter.ParlourHorizontalAdapter
import com.thekainchee.user.presentation.dashboard.home.adapter.ParlourVerticalAdapter
import com.thekainchee.user.presentation.dashboard.home.model.ParlourUI
import com.thekainchee.user.presentation.dashboard.home.state.ParlourState
import com.thekainchee.user.presentation.dashboard.home.viewModel.LocationViewModel
import com.thekainchee.user.presentation.dashboard.home.viewModel.ParlourViewModel
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.presentation.parlour.ParlourActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint

class MensParlourFragment : Fragment() {
    private  var _binding : FragmentAllParlourBinding? = null
    private val binding get() = _binding!!
    private lateinit var nearbyAdapter : ParlourVerticalAdapter
    private lateinit var trendingAdapter : ParlourHorizontalAdapter
    private val viewModel : LocationViewModel by activityViewModels()
    private val parlourViewModel: ParlourViewModel by viewModels()

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
        binding.rvNearbyParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.layoutTrendingServicesSection.isVisible = false
        binding.layoutBookingsSection.isVisible = false
        val dummyParlours = listOf(
            ParlourUI(
                id = "1",
                name = "Royal Mens Salon",
                image = "https://images.unsplash.com/photo-1599351431202-1e0f0137899a",
                rating = 4.5,
                distance = 0.8,
                type = "MENS"
            ),
            ParlourUI(
                id = "2",
                name = "Elite Gents Studio",
                image = "https://images.unsplash.com/photo-1503951914875-452162b0f3f1",
                rating = 4.6,
                distance = 1.1,
                type = "MENS"
            ),
            ParlourUI(
                id = "3",
                name = "Urban Mens Hub",
                image = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70",
                rating = 4.3,
                distance = 2.0,
                type = "MENS"
            ),
            ParlourUI(
                id = "4",
                name = "Gentlemen's Lounge",
                image = "https://images.unsplash.com/photo-1519415943484-9fa1873496d4",
                rating = 4.8,
                distance = 0.5,
                type = "MENS"
            ),
            ParlourUI(
                id = "5",
                name = "Classic Barber Shop",
                image = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438",
                rating = 4.4,
                distance = 1.7,
                type = "MENS"
            ),
            ParlourUI(
                id = "6",
                name = "King's Cut Salon",
                image = "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f",
                rating = 4.7,
                distance = 2.3,
                type = "MENS"
            )
        )
        binding.btnRetry.setOnClickListener {
            hideFullEmpty()
            parlourViewModel.getNearbyParlours(type = "MENS", forceRefresh = true)
            parlourViewModel.trendingParlours(type = "MENS")
        }

        binding.btnChangeLocation.setOnClickListener {
            val intent = Intent(requireContext(), LocationActivity::class.java)
            startActivity(intent)
        }
        nearbyAdapter = ParlourVerticalAdapter ( onItemClick = { item ->
            val intent = Intent(context, ParlourActivity::class.java)
            intent.putExtra("parlourId", "6954d86cfe7152f0c3ced6fb")
            intent.putExtra("distance", "2.3")
            startActivity(intent)
        })

        trendingAdapter = ParlourHorizontalAdapter ( onItemClick = { item ->
            val intent = Intent(context, ParlourActivity::class.java)
            intent.putExtra("parlourId", item.id)
            intent.putExtra("distance", item.distance)
            startActivity(intent)
        })
        binding.rvNearbyParlours.adapter = nearbyAdapter
        binding.rvTrendingParlours.adapter = trendingAdapter
        nearbyAdapter.submitList(dummyParlours)
        trendingAdapter.submitList(dummyParlours)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch{
                    viewModel.location.collect {address ->
                        address?.let {
                            parlourViewModel.setLocation(it.latitude,it.longitude)
//                            parlourViewModel.getNearbyParlours(type = "MENS")
//                            parlourViewModel.trendingParlours(type = "MENS")
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
            }
        }
        binding.rvNearbyParlours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem >= totalItemCount - 2) {

                    parlourViewModel.nearbyLoadNextPage("MENS")
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