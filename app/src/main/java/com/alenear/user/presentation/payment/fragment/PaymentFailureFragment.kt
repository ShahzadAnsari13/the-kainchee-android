package com.alenear.user.presentation.payment.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.alenear.user.R
import com.alenear.user.databinding.FragmentPaymentFailureBinding
import com.alenear.user.databinding.FragmentVerifyingPaymentBinding
import com.alenear.user.presentation.booking.BookingActivity
import com.google.android.material.snackbar.Snackbar

class PaymentFailureFragment : Fragment() {

    private var _binding : FragmentPaymentFailureBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentFailureBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    openHome()
                }
            }
        )

        binding.btnHome.setOnClickListener {
            openHome()
        }
    }


    private fun openHome() {
        val intent = Intent(requireContext(), BookingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}