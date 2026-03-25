package com.thekainchee.user.presentation.dashboard.home.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentHomeBinding
import com.thekainchee.user.presentation.common.bottomSheet.LocationPermissionBottomSheet
import com.thekainchee.user.presentation.dashboard.home.viewModel.HomeViewModel
import com.thekainchee.user.presentation.dashboard.home.adapter.HomeTabsAdapter
import com.thekainchee.user.presentation.location.LocationActivity
import com.thekainchee.user.utils.LocationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.security.Permission

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : HomeTabsAdapter
    private  val viewModel : HomeViewModel by viewModels()
    private var openedPermissionSettings = false

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fine || coarse) {
                viewModel.fetchUserLocation()
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
                    viewModel.fetchUserLocation()
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
        LocationUtils.checkGpsStatus(
            requireActivity(),
            gpsResolutionLauncher
        ) {
            LocationUtils.checkLocationPermission(
                activity = requireActivity(),
                launcher = locationPermissionLauncher
            ) {
                viewModel.fetchUserLocation()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.location.collect { address ->

                    address?.let {

                        val raw = "Selected Location - ${it.city}, ${it.district}"
                        val text = if (raw.length > 30) raw.take(30) + "..." else raw

                        val spannable = SpannableString(" $text ")

                        val locationIcon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_location)

                        val downIcon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_down)

                        val locationSize = 45
                        val size = 45

                        locationIcon?.setBounds(0, 0, locationSize, locationSize)
                        downIcon?.setBounds(0, 0, size, size)

                        // start icon
                        locationIcon?.let {
                            spannable.setSpan(
                                ImageSpan(it, ImageSpan.ALIGN_BOTTOM),
                                0,
                                1,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }

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
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            LocationActivity::class.java
                                        )
                                    )
                                }
                            },
                            0,
                            spannable.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        binding.tvLocation.movementMethod =
                            LinkMovementMethod.getInstance()

                        binding.tvLocation.text = spannable
                    }

                }
            }
        }




        adapter = HomeTabsAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "All"
                1 -> tab.text = "Mens"
                2 -> tab.text = "Beauty"
                3 -> tab.text = "Unisex"
            }
        }.attach()
        binding.viewPager.offscreenPageLimit = 4

    }




    override fun onResume() {
        super.onResume()
        if (openedPermissionSettings) {

            openedPermissionSettings = false

            LocationUtils.checkLocationPermission(
                activity = requireActivity(),
                launcher = locationPermissionLauncher
            ) {
                viewModel.fetchUserLocation()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}