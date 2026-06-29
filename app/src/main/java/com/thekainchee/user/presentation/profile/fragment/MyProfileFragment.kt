package com.thekainchee.user.presentation.profile.fragment

import android.R.attr.text
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {
    private var _binding : FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAccountSection()
        setupSupportSection()
        binding.quickActions.actionBookings.apply {
            ivIcon.setImageResource(R.drawable.ic_booking_3d)
            tvTitle.text = "Bookings"
            tvSubtitle.text = "View & Manage"
        }

        binding.quickActions.actionAddress.apply {
            ivIcon.setImageResource(R.drawable.ic_location_3d)
            tvTitle.text = "Addresses"
            tvSubtitle.text = "Manage Addresses"
        }

        binding.quickActions.actionNotification.apply {
            ivIcon.setImageResource(R.drawable.ic_notification_3d)
            tvTitle.text = "Alerts"
            tvSubtitle.text = "On"
        }

        binding.quickActions.actionRefer.apply {
            ivIcon.setImageResource(R.drawable.ic_gift)
            tvTitle.text = "Refer"
            tvSubtitle.text = "Invite"
        }
    }
    private fun setupAccountSection() {

        with(binding.accountSection.itemPersonalInfo) {
            ivIcon.setImageResource(R.drawable.ic_person)
            tvTitle.text = "Personal Information"
            tvSubtitle.text = "Update your personal details"
            tvStatus.isGone = true
        }

        with(binding.accountSection.itemPhoneNumber) {
            ivIcon.setImageResource(R.drawable.ic_phone_3d)
            tvTitle.text = "Phone Number"
            tvSubtitle.text = "Verify or change your phone"
            tvStatus.isGone = true
        }

        with(binding.accountSection.itemAccountSecurity) {
            ivIcon.setImageResource(R.drawable.ic_security)
            tvTitle.text = "Account Security"
            tvSubtitle.text = "Password & account protection"
            tvStatus.isGone = true
        }

        with(binding.accountSection.itemAccountStatus) {
            ivIcon.setImageResource(R.drawable.ic_verified)
            tvTitle.text = "Account Status"
            tvSubtitle.text = "Your account is active"

            tvStatus.apply {
                isGone = false
                text = "Active"
            }
        }
    }
    private fun setupSupportSection() {

        with(binding.supportSection.itemHelpSupport) {
            ivIcon.setImageResource(R.drawable.ic_support)
            tvTitle.text = "Help & Support"
            tvSubtitle.text = "Get assistance anytime"
            tvStatus.isGone = true
        }

        with(binding.supportSection.itemPrivacyPolicy) {
            ivIcon.setImageResource(R.drawable.ic_lock)
            tvTitle.text = "Privacy Policy"
            tvSubtitle.text = "Read our privacy policy"
            tvStatus.isGone = true
        }

        with(binding.supportSection.itemTermsConditions) {
            ivIcon.setImageResource(R.drawable.ic_description)
            tvTitle.text = "Terms & Conditions"
            tvSubtitle.text = "Know our terms of use"
            tvStatus.isGone = true
        }

        with(binding.supportSection.itemAboutApp) {
            ivIcon.setImageResource(R.drawable.ic_info_3d)
            tvTitle.text = "About App"
            tvSubtitle.text = "Version 1.0.0"
            tvStatus.isGone = true
        }
    }
}