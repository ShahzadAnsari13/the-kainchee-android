package com.thekainchee.user.presentation.booking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentBookingSlotBinding
import com.thekainchee.user.presentation.booking.adapter.BookingSlotAdapter
import com.thekainchee.user.presentation.booking.adapter.BookingStaffAdapter
import com.thekainchee.user.presentation.booking.model.SlotUiModel
import com.thekainchee.user.presentation.booking.state.SlotState
import com.thekainchee.user.presentation.booking.state.StaffState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class BookingSlotFragment : Fragment() {

    private val navArgs : BookingSlotFragmentArgs by navArgs()

    private val viewModel : BookingViewModel by viewModels()
    private val bookingPreviewData by lazy {
        navArgs.services
    }
    private lateinit var bookingStaffAdapter: BookingStaffAdapter
    private lateinit var bookingSlotAdapter: BookingSlotAdapter
    private var _binding : FragmentBookingSlotBinding? = null
    private val binding get() = _binding!!
    private val currentDate = LocalDate.now().toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingSlotBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingStaffAdapter = BookingStaffAdapter {staff ->
            viewModel.getStaffSlots(bookingPreviewData.parlourId,staff.id,currentDate)
        }
        bookingSlotAdapter = BookingSlotAdapter{

        }
        binding.rvStaff.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false

            )

            adapter = bookingStaffAdapter
        }

        binding.rvSlots.apply {
            layoutManager = GridLayoutManager(requireContext(),3)

            adapter = bookingSlotAdapter
        }
        val dummySlots = listOf(

            SlotUiModel("10:00 AM"),
            SlotUiModel("11:00 AM"),
            SlotUiModel("12:00 PM"),
            SlotUiModel("01:00 PM"),
            SlotUiModel("02:00 PM"),
            SlotUiModel("03:00 PM"),
            SlotUiModel("04:00 PM"),
            SlotUiModel("05:00 PM"),
            SlotUiModel("06:00 PM")

        )

        val firstService = bookingPreviewData.services.firstOrNull() ?: return
        binding.tvServiceTitle.text ="${firstService.name} + ${bookingPreviewData.totalServices-1}"
        binding.tvServiceInfo.text = "⏱ ${bookingPreviewData.totalDuration} min • ₹${bookingPreviewData.totalPrice}"
        Glide.with(this@BookingSlotFragment)
            .load(firstService.image)
            .placeholder(R.drawable.ic_oops)
            .into(binding.imgService)

        viewModel.getParlourStaffs(bookingPreviewData.parlourId)


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.staffState.collect {state->
                        when(state){
                            is StaffState.Idle -> {

                            }
                            is StaffState.Loading -> {
                                binding.rvStaff.visibility = View.GONE
                                binding.rvSlots.visibility = View.GONE
                                binding.shimmerStaffLayout.visibility = View.VISIBLE
                                binding.shimmerStaffLayout.startShimmer()
                            }
                            is StaffState.Success -> {
                                binding.shimmerStaffLayout.stopShimmer()
                                binding.shimmerStaffLayout.visibility = View.GONE
                                binding.rvStaff.visibility = View.VISIBLE
                                binding.rvSlots.visibility = View.VISIBLE
                                bookingStaffAdapter.submitList(state.data)
                                bookingSlotAdapter.submitList(dummySlots)
                            }
                            is StaffState.Empty -> {
                                binding.shimmerStaffLayout.stopShimmer()
                                binding.shimmerStaffLayout.visibility = View.GONE
                                binding.rvStaff.visibility = View.GONE
                                binding.rvSlots.visibility = View.GONE
                            }
                            is StaffState.Error -> {
                                binding.shimmerStaffLayout.stopShimmer()
                                binding.shimmerStaffLayout.visibility = View.GONE
                                binding.rvStaff.visibility = View.GONE
                                binding.rvSlots.visibility = View.GONE
                            }
                        }
                    }
                }
                launch {
                    viewModel.slotState.collect {state->
                        when(state){
                            is SlotState.Idle -> {

                            }
                            is SlotState.Loading -> {
                                binding.rvSlots.visibility = View.GONE
                                binding.shimmerSlotLayout.visibility = View.VISIBLE
                                binding.shimmerSlotLayout.startShimmer()
                            }
                            is SlotState.Success -> {
                                binding.shimmerSlotLayout.stopShimmer()
                                binding.shimmerSlotLayout.visibility = View.GONE
                                binding.rvSlots.visibility = View.VISIBLE
                                bookingSlotAdapter.submitList(state.data)
                            }
                            is SlotState.Empty -> {
                                binding.shimmerSlotLayout.stopShimmer()
                                binding.shimmerSlotLayout.visibility = View.GONE
                                binding.rvSlots.visibility = View.GONE
                            }
                            is SlotState.Error -> {
                                binding.shimmerSlotLayout.stopShimmer()
                                binding.shimmerSlotLayout.visibility = View.GONE
                                binding.rvSlots.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }
}