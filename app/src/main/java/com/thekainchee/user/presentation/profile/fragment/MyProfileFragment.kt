package com.thekainchee.user.presentation.profile.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentMyProfileBinding
import com.thekainchee.user.presentation.booking.BookingActivity
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.presentation.profile.ProfileActivity
import com.thekainchee.user.presentation.profile.bottomSheet.EditProfileBottomSheet
import com.thekainchee.user.presentation.profile.model.ProfileUiModel
import com.thekainchee.user.presentation.profile.state.EditProfileEvent
import com.thekainchee.user.presentation.profile.state.ProfileState
import com.thekainchee.user.presentation.profile.viewModel.ProfileViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.jvm.java

@AndroidEntryPoint
class MyProfileFragment : Fragment() {
    private var _binding : FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private var isSwipeRefresh  = false
    private var profile: ProfileUiModel? = null
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
        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.mainContent.visibility = View.GONE
            binding.layoutNoInternet.visibility = View.VISIBLE
        }else{
            profileViewModel.getProfile()
        }
        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(
                    binding.root,
                    "No Internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                profileViewModel.getProfile()
            }
        }
        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
               binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                profileViewModel.getProfile()
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.errorLayout.visibility = View.GONE
                binding.layoutNoInternet.visibility = View.VISIBLE
            }else{
                isSwipeRefresh  =  true
                profileViewModel.getProfile()
            }
        }
        binding.quickActions.actionAddress.root.setOnClickListener {
            startActivity(Intent(requireContext(), LocationActivity::class.java))
        }
        binding.quickActions.actionBookings.root.setOnClickListener {
            val intent = Intent(requireContext(), BookingActivity::class.java)
            intent.putExtra("openMyBookings", true)
            startActivity(intent)
        }
        binding.profileHeaderCard.btnEditProfile.setOnClickListener {

            profile?.let {
                EditProfileBottomSheet.newInstance(
                    it.name,
                    it.countryCode,
                    it.phoneNumber
                ).show(parentFragmentManager, "EditProfile")
            } ?: run {
                Snackbar.make(
                    binding.root,
                    "Something went wrong",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        binding.walletCard.layoutTransaction.setOnClickListener {
            findNavController().navigate(
                MyProfileFragmentDirections
                    .actionMyProfileFragmentToWalletTransactionFragment(
                        profile?.walletBalance?.toFloat() ?: 0f
                    )
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                profileViewModel.profileState.collect{state ->
                    when(state){
                        is ProfileState.Idle -> {

                        }
                        is ProfileState.Loading -> {
                            if(!isSwipeRefresh ){
                                binding.shimmerLayout.visibility = View.VISIBLE
                                binding.mainContent.visibility = View.GONE
                                binding.layoutNoInternet.visibility = View.GONE
                                binding.errorLayout.visibility = View.GONE
                                binding.shimmerLayout.startShimmer()
                            }

                        }

                        is ProfileState.Success -> {
                            if(!isSwipeRefresh ){

                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE
                            }else{
                                binding.swipeRefresh.isRefreshing = false
                                isSwipeRefresh  = false
                            }
                            binding.layoutNoInternet.visibility = View.GONE
                            binding.errorLayout.visibility = View.GONE
                            binding.mainContent.visibility = View.VISIBLE
                            profile = state.data
                            binding.profileHeaderCard.tvName.text = state.data.name
                            binding.profileHeaderCard.tvPhone.text = "${state.data.countryCode} ${state.data.phoneNumber}"
                            binding.profileHeaderCard.tvMemberSince.text ="Member since ${state.data.memberSince}"
                            binding.walletCard.tvWalletBalance.text = "${"₹%.2f".format(state.data.walletBalance)}"
                            binding.accountSection.itemAccountStatus.tvStatus.text =
                            if (state.data.isActive) "Active" else "Inactive"
                            if(state.data.notificationsEnabled){

                                binding.quickActions.actionNotification.tvSubtitle.text  = "\uD83D\uDFE2 On"
                            }else{
                                binding.quickActions.actionNotification.tvSubtitle.text  = "\uD83D\uDD34 Off"
                            }
                        }
                        is ProfileState.Error -> {
                            if(!isSwipeRefresh ){
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE
                            }else{
                                binding.swipeRefresh.isRefreshing = false
                                isSwipeRefresh  = false
                            }
                            binding.mainContent.visibility = View.GONE
                            binding.layoutNoInternet.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE

                        }

                    }

                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                profileViewModel.event.collect{event ->
                    when(event){
                        is EditProfileEvent.Success -> {
                            binding.profileHeaderCard.tvName.text = event.data
                        }
                        is EditProfileEvent.Error -> {
                            Snackbar.make(requireView(), event.message, Snackbar
                                .LENGTH_SHORT).show()
                        }
                    }
                }
            }
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
    override fun onResume() {
        super.onResume()
        (requireActivity() as ProfileActivity).setToolbarTitle("My Profile")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}