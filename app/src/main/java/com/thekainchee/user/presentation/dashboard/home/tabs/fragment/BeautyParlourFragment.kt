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
class BeautyParlourFragment : Fragment() {
   private var  _binding : FragmentAllParlourBinding? = null
    private val binding get() = _binding!!
    private lateinit var nearbyAdapter : ParlourVerticalAdapter
    private lateinit var trendingAdapter : ParlourHorizontalAdapter
    private val viewModel : LocationViewModel by activityViewModels()
    private val parlourViewModel: ParlourViewModel by viewModels()
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
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTrendingParlours.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.layoutTrendingServicesSection.isVisible = false
        binding.layoutBookingsSection.isVisible = false
        val dummyParlours = listOf(
            ParlourUI(
                id = "6",
                name = "Glow Beauty Parlour",
                image = "https://images.unsplash.com/photo-1595476108010-b4d1f102b1b1",
                rating = 4.6,
                distance = 1.0,
                type = "BEAUTY"
            ),
            ParlourUI(
                id = "7",
                name = "Ladies Beauty Hub",
                image = "https://images.unsplash.com/photo-1527799820374-dcf8d9d4a388",
                rating = 4.4,
                distance = 2.3,
                type = "BEAUTY"
            ),
            ParlourUI(
                id = "8",
                name = "Queen Makeover Studio",
                image = "https://images.unsplash.com/photo-1512496015851-a90fb38ba796",
                rating = 4.7,
                distance = 0.9,
                type = "BEAUTY"
            ),
            ParlourUI(
                id = "9",
                name = "Divine Beauty Lounge",
                image = "https://images.unsplash.com/photo-1559599101-f09722fb4948",
                rating = 4.5,
                distance = 1.5,
                type = "BEAUTY"
            ),
            ParlourUI(
                id = "10",
                name = "Pink Blush Studio",
                image = "https://images.unsplash.com/photo-1560066984-138dadb4c035",
                rating = 4.8,
                distance = 2.0,
                type = "BEAUTY"
            )
        )
        binding.btnRetry.setOnClickListener {
            hideFullEmpty()
            parlourViewModel.getNearbyParlours(type = "BEAUTY", forceRefresh = true)
            parlourViewModel.trendingParlours(type = "BEAUTY")
        }

        binding.btnChangeLocation.setOnClickListener {
            val intent = Intent(requireContext(), LocationActivity::class.java)
            startActivity(intent)
        }
        nearbyAdapter = ParlourVerticalAdapter ( onItemClick = { item ->
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
                            parlourViewModel.getNearbyParlours(type = "BEAUTY")
                            parlourViewModel.trendingParlours(type = "BEAUTY")
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

                    parlourViewModel.nearbyLoadNextPage("BEAUTY")
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