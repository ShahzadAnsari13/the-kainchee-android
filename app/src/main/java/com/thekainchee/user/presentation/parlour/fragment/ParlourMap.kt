package com.thekainchee.user.presentation.parlour.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentParlourMapBinding
import com.thekainchee.user.presentation.location.state.MapState
import com.thekainchee.user.presentation.location.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ParlourMap : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentParlourMapBinding? = null
    private val binding get() = _binding!!

    private val args: ParlourMapArgs by navArgs()
    private val viewModel: MapViewModel by viewModels()

    private var googleMap: GoogleMap? = null
    private var latLng: LatLng? = null
    private var isMapReady = false
    private var isApiLoaded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParlourMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Safe lat/lng parse
        val latitude = args.latitude.toDoubleOrNull()
        val longitude = args.longitude.toDoubleOrNull()

        if (latitude == null || longitude == null) {
            Toast.makeText(requireContext(), "Invalid location", Toast.LENGTH_SHORT).show()
            return
        }

        latLng = LatLng(latitude, longitude)

        //  Get address from lat/lng
        viewModel.getAddressFromLatLng(latitude, longitude)

        //  Map setup
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.btnGetDirection.setOnClickListener {

            val lat = latLng?.latitude ?: return@setOnClickListener
            val lng = latLng?.longitude ?: return@setOnClickListener

            try {
                // Direct Google Maps navigation (BEST UX)
                val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                startActivity(mapIntent)

            } catch (e: Exception) {
                // Fallback (browser open ho jayega agar Maps nahi hai)
                val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
        //  Observe state
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when (state) {

                        // 🔹 LOADING (API)
                        is MapState.Loading -> {

                            isApiLoaded = false

                            binding.cardShimmer.startShimmer()
                            binding.cardShimmer.visibility = View.VISIBLE
                            binding.locationCard.visibility = View.GONE
                        }

                        // 🔹 SUCCESS
                        is MapState.AddressReceived -> {

                            isApiLoaded = true
                            binding.cardShimmer.stopShimmer()
                            binding.cardShimmer.visibility = View.GONE
                            binding.locationCard.visibility = View.VISIBLE
                            val address = state.address

                            binding.tvhead.text =
                                address.details ?: address.city ?: ""

                            val fullAddress = listOf(
                                address.details,
                                address.city,
                                address.state,
                                address.pincode
                            )
                                .filter { !it.isNullOrBlank() }
                                .joinToString(", ")

                            binding.tvfullAdd.text = fullAddress

                            //  shimmer hide + data show


                        }

                        // 🔹 ERROR
                        is MapState.Error -> {

                            isApiLoaded = false

                            binding.cardShimmer.stopShimmer()
                            binding.cardShimmer.visibility = View.GONE

                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }

    }

    //  Map ready
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        isMapReady = true
        binding.fullShimmer.stopShimmer()
        binding.fullShimmer.visibility = View.GONE
        if (isApiLoaded) {
            binding.cardShimmer.stopShimmer()
            binding.cardShimmer.visibility = View.GONE
            binding.locationCard.visibility = View.VISIBLE
        } else {
            binding.cardShimmer.startShimmer()
            binding.cardShimmer.visibility = View.VISIBLE
            binding.locationCard.visibility = View.GONE
        }
        // MAP SHOW
        binding.mapContainer.visibility = View.VISIBLE
        latLng?.let {
            googleMap?.apply {

                //  important: bottom card ke upar marker visible rahe
                setPadding(0, 0, 0, 250)

                clear()

                addMarker(
                    MarkerOptions()
                        .position(it)
                        .title("Parlour Location")
                )

                moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}