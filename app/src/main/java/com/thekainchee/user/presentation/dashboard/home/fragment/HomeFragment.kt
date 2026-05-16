package com.thekainchee.user.presentation.dashboard.home.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentHomeBinding
import com.thekainchee.user.presentation.common.bottomSheet.LocationPermissionBottomSheet
import com.thekainchee.user.presentation.dashboard.home.viewModel.LocationViewModel
import com.thekainchee.user.presentation.dashboard.home.adapter.HomeTabsAdapter
import com.thekainchee.user.presentation.dashboard.home.state.LocationUiState
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.utils.LocationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : HomeTabsAdapter
    private  val locationViewModel : LocationViewModel by activityViewModels()
    private var openedPermissionSettings = false
    private var shouldRefreshLocation = false
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fine || coarse) {
                locationViewModel.fetchUserLocation()
            } else {
                LocationPermissionBottomSheet {
                    openedPermissionSettings = true
                }.show(parentFragmentManager, "LocationPermissionBottomSheet")
            }
        }

    private val gpsResolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                LocationUtils.checkLocationPermission(
                    activity = requireActivity(),
                    launcher = locationPermissionLauncher
                ) {
                    locationViewModel.fetchUserLocation()
                }

            } else {
                requireActivity().finishAffinity()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGreeting.text = getGreeting()
        LocationUtils.checkGpsStatus(
            requireActivity(),
            gpsResolutionLauncher
        ) {
            LocationUtils.checkLocationPermission(
                activity = requireActivity(),
                launcher = locationPermissionLauncher
            ) {
                locationViewModel.fetchUserLocation()
            }
        }
        observeLocation()
        handleOnBackPressed()



        adapter = HomeTabsAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "All"
                1 -> tab.text = "Mens"
                2 -> tab.text = "Beauty"
                3 -> tab.text = "Unisex"
            }
        }.attach()



    }

    private fun observeLocation(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                locationViewModel.location.collect { state ->
                    when(state){
                        is LocationUiState.Idle ->{

                        }
                        is LocationUiState.Loading -> {
                            binding.tvLocation.text = "\uD83D\uDCCD Fetching location..."
                        }

                        is LocationUiState.Success -> {

                            val raw = "\uD83D\uDCCD Selected Location - ${
                                listOf(state.address.details, state.address.city)
                                    .filter { !it.isNullOrBlank() }
                                    .joinToString(", ")
                            }"
                            val text = if (raw.length > 30) raw.take(30) + "..." else raw

                            val spannable = SpannableString(" $text ")

                            val downIcon =
                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_down)

                            val size = 45

                            downIcon?.setBounds(0, 0, size, size)



                            // end icon
                            downIcon?.let {
                                spannable.setSpan(
                                    ImageSpan(it, ImageSpan.ALIGN_BOTTOM),
                                    spannable.length - 1,
                                    spannable.length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }

                            // clickable arrow
                            spannable.setSpan(
                                object : ClickableSpan() {
                                    override fun onClick(widget: View) {
                                        shouldRefreshLocation = true
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                LocationActivity::class.java
                                            )
                                        )
                                    }
                                },
                                spannable.length-1,
                                spannable.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            setLocationText(spannable)
                        }

                        is LocationUiState.Error -> {
                            val text = "\uD83D\uDCCD Unable to load location  Retry"

                            val spannable = SpannableString(text)

                            val retryStart = text.indexOf("Retry")
                            val retryEnd = retryStart + "Retry".length
                            spannable.setSpan(
                                object : ClickableSpan() {
                                    override fun onClick(widget: View) {
                                        locationViewModel.fetchUserLocation()
                                    }
                                    override fun updateDrawState(ds: TextPaint) {
                                        super.updateDrawState(ds)
                                        ds.isUnderlineText = false
                                        ds.isFakeBoldText = true
                                        ds.color = ContextCompat.getColor(
                                            requireContext(),
                                            R.color.primaryColor
                                        )
                                    }
                                },
                                retryStart,
                                retryEnd,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE


                            )

                            setLocationText(spannable)


                        }
                    }


                }
            }
        }
    }

    private fun getGreeting():String{
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good Morning ☕"
            in 12..16 -> "Good Afternoon 🌤"
            else -> "Good Evening ✨"
        }
    }

    override fun onResume() {
        super.onResume()
        if (openedPermissionSettings) {

            openedPermissionSettings = false

            LocationUtils.checkLocationPermission(
                activity = requireActivity(),
                launcher = locationPermissionLauncher
            ) {
                locationViewModel.fetchUserLocation()
            }
        }else if (shouldRefreshLocation) {

            shouldRefreshLocation = false
            locationViewModel.fetchUserLocation()
        }
    }

    private fun handleOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (binding.viewPager.currentItem != 0) {

                        binding.viewPager.currentItem = 0

                    } else {

                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }
    private fun setLocationText(spannable: SpannableString) {
        binding.tvLocation.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = spannable
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}