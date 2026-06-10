package com.thekainchee.user.presentation.booking.bottomSheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentPaymentMethodBottomSheetBinding
import com.thekainchee.user.databinding.LayoutPaymentBottomSheetBinding

class PaymentMethodBottomSheet : BottomSheetDialogFragment() {
    private var _binding: LayoutPaymentBottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutPaymentBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}