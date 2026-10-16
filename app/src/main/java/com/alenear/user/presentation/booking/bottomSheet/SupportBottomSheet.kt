package com.alenear.user.presentation.booking.bottomSheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.alenear.user.R
import com.alenear.user.databinding.FragmentSupportBottomSheetBinding


class SupportBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentSupportBottomSheetBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val SUPPORT_NUMBER = "+919876543210"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupportBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.btnCallSupport.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:$SUPPORT_NUMBER")
            )

            startActivity(intent)
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()

        val bottomSheet =
            dialog?.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) ?: return

        val behavior = BottomSheetBehavior.from(bottomSheet)

        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        bottomSheet.layoutParams.height = (screenHeight * 0.94).toInt()
        bottomSheet.requestLayout()

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = true
    }

    override fun onDestroyView() {

        _binding = null

        super.onDestroyView()
    }
}