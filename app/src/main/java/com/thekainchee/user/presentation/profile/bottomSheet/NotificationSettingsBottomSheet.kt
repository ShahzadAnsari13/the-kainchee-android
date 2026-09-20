package com.thekainchee.user.presentation.profile.bottomSheet

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentNotificationSettingsBottomSheetBinding

class NotificationSettingsBottomSheet : BottomSheetDialogFragment() {


    private var _binding: FragmentNotificationSettingsBottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationSettingsBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }
    private fun setupClickListeners() {

        binding.cardClose.setOnClickListener {
            dismiss()
        }

        binding.tvNotNow.setOnClickListener {
            dismiss()
        }

        binding.btnOpenSettings.setOnClickListener {
            dismiss()
            openNotificationSettings()
        }
    }


    private fun openNotificationSettings() {

        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(
                    Settings.EXTRA_APP_PACKAGE,
                    requireContext().packageName
                )
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse(
                    "package:${requireContext().packageName}"
                )
            }
        }

        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}