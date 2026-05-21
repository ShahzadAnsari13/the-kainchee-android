package com.thekainchee.user.presentation.service.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentServiceListBinding
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.presentation.service.adapter.ServiceAdapter
import com.thekainchee.user.presentation.service.bottomSheet.BookingPreviewBottomSheet
import com.thekainchee.user.presentation.service.model.ServiceUiModel
import com.thekainchee.user.presentation.service.state.BookingPreviewEvent
import com.thekainchee.user.presentation.service.state.ServiceListState
import com.thekainchee.user.presentation.service.viewModel.ServiceViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceListFragment : Fragment() {

    private var _binding : FragmentServiceListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ServiceAdapter
    private val serviceViewModel : ServiceViewModel by activityViewModels()
    private val navArgs : ServiceListFragmentArgs by navArgs()
    private var parlourId : String? = null
    private var categoryId : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentServiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as ParlourActivity)
            .setToolbarTitle(navArgs.categoryName +" Services" ?: "Services")
        parlourId = navArgs.parlourId
        categoryId = navArgs.categoryId

        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.contentLayout.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }else{
            parlourId?.let { parlourId ->
                categoryId?.let { categoryId ->
                    serviceViewModel.getServicesByCategory(false,parlourId,categoryId)
                }
            }
        }

        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                parlourId?.let { parlourId ->
                    categoryId?.let { categoryId ->
                        serviceViewModel.getServicesByCategory(false,parlourId,categoryId)
                    }
                }
            }
        }

        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                parlourId?.let{parlourId ->
                    serviceViewModel.getBookingPreview(parlourId,serviceViewModel.selectedServiceIds.value)
                }
            }
        }

        binding.bottomBookingStrip.setOnClickListener {
            Log.d("CLICK_TEST", "Clicked")
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                parlourId?.let { parlourId ->
                    Log.d("parlourId",parlourId)
                    serviceViewModel.getBookingPreview(parlourId,serviceViewModel.selectedServiceIds.value)
                }

            }
        }

        adapter = ServiceAdapter(onAddClick = {item->
            val updatedList = adapter.currentList.map {

                if (it.id == item.id) {
                    it.copy(isAdded = true)
                } else {
                    it
                }
            }
            parlourId?.let { id ->
                serviceViewModel.addService(id, item.id)
            }

            adapter.submitList(updatedList)
        }, onRemoveClick = {item ->
            val updatedList = adapter.currentList.map {
                if (it.id == item.id) {
                    it.copy(isAdded = false)
                } else {
                    it
                }
            }
            parlourId?.let { id ->
                serviceViewModel.removeService(id, item.id)
            }

            adapter.submitList(updatedList)
        })
        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ServiceListFragment.adapter

        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    serviceViewModel.serviceListState.collect{ state ->
                        when(state){
                            is ServiceListState.Idle -> {

                            }
                            is ServiceListState.Loading -> {
                                binding.shimmer.visibility = View.VISIBLE
                                binding.contentLayout.visibility = View.GONE
                                binding.errorLayout.visibility = View.GONE
                                binding.layoutNoInternet.visibility = View.GONE
                                binding.shimmer.startShimmer()
                            }
                            is ServiceListState.Success -> {
                                binding.shimmer.stopShimmer()
                                binding.shimmer.visibility = View.GONE
                                binding.contentLayout.visibility = View.VISIBLE
                                binding.errorLayout.visibility = View.GONE
                                adapter.submitList(state.data)
                                parlourId?.let { id ->
                                    serviceViewModel.loadSelectedServices(id)
                                }
                            }
                            is ServiceListState.Empty -> {
                                binding.shimmer.stopShimmer()
                                binding.shimmer.visibility = View.GONE
                                binding.errorLayout.visibility = View.VISIBLE
                                binding.btnRetry.visibility  =View.GONE
                                binding.contentLayout.visibility = View.GONE
                                binding.tvEmptyTitle.text = "No Services Found"
                                binding.tvEmptySubtitle.text = "This category has no services yet."
                            }
                            is ServiceListState.Error -> {
                                binding.shimmer.stopShimmer()
                                binding.shimmer.visibility = View.GONE
                                binding.errorLayout.visibility = View.VISIBLE
                                binding.contentLayout.visibility = View.GONE
                                binding.tvEmptyTitle.text = "Something went wrong"
                                binding.tvEmptySubtitle.text = state.message
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

                                            refreshAfterBottomSheet()
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

    private fun showBottomStrip() {

        binding.bottomBookingStrip.apply {

            if (visibility == View.VISIBLE) return

            visibility = View.VISIBLE
            alpha = 0f
            translationX =binding.bottomBookingStrip.width.toFloat()

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

    fun refreshAfterBottomSheet() {
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            return
        }

        parlourId?.let { parlourId ->
            categoryId?.let { categoryId ->

                serviceViewModel.getServicesByCategory(
                    force = true,
                    parlourId = parlourId,
                    categoryId = categoryId
                )
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.shimmer.stopShimmer()
        _binding = null
    }

}
