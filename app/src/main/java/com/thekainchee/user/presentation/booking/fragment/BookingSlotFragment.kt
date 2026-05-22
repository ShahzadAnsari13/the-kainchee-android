package com.thekainchee.user.presentation.booking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentBookingSlotBinding
import com.thekainchee.user.presentation.booking.adapter.BookingStaffAdapter
import com.thekainchee.user.presentation.booking.model.RatingModel
import com.thekainchee.user.presentation.booking.model.StaffUiModel

class BookingSlotFragment : Fragment() {

    private val navArgs : BookingSlotFragmentArgs by navArgs()

    private val bookingPreviewData by lazy {
        navArgs.services
    }
    private lateinit var bookingStaffAdapter: BookingStaffAdapter
    private var _binding : FragmentBookingSlotBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingSlotBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingStaffAdapter = BookingStaffAdapter {

        }
        binding.rvStaff.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false

            )

            adapter = bookingStaffAdapter
        }

        val firstService = bookingPreviewData.services.firstOrNull() ?: return
        binding.tvServiceTitle.text ="${firstService.name} + ${bookingPreviewData.totalServices-1}"
        binding.tvServiceInfo.text = "⏱ ${bookingPreviewData.totalDuration} min • ₹${bookingPreviewData.totalPrice}"
        Glide.with(this@BookingSlotFragment)
            .load(firstService.image)
            .placeholder(R.drawable.ic_oops)
            .into(binding.imgService)







        val dummyStaffList = listOf(

            StaffUiModel(
                id = "1",
                name = "Aman",
                image = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                experience = 5,
                rating = RatingModel(
                    average = 4.8f,
                    count = 120
                ), phone = "7667866691"
            ),

            StaffUiModel(
                id = "2",
                name = "Rahul",
                image = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d",
                experience = 3,
                rating = RatingModel(
                    average = 4.6f,
                    count = 80
                ), phone = "7667866696"
            ),

            StaffUiModel(
                id = "3",
                name = "Danish",
                image = "https://images.unsplash.com/photo-1504257432389-52343af06ae3",
                experience = 7,
                rating = RatingModel(
                    average = 4.9f,
                    count = 200
                ), phone = "7667866691"
            ),

            StaffUiModel(
                id = "4",
                name = "Shahid",
                image = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce",
                experience = 4,
                rating = RatingModel(
                    average = 4.7f,
                    count = 95
                ), phone = "7667866691"
            )
        )


        bookingStaffAdapter.submitList(dummyStaffList)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }
}