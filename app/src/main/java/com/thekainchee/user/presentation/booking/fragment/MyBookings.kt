package com.thekainchee.user.presentation.booking.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentMyBookingsBinding

class MyBookings : Fragment() {
    private var _binding : FragmentMyBookingsBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBookingsBinding.inflate(inflater,container,false)
        return binding.root
    }


}