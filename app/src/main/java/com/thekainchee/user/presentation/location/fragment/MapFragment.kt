package com.thekainchee.user.presentation.location.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentMapBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.Places
import com.thekainchee.user.presentation.location.MapState
import com.thekainchee.user.presentation.location.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var placeId: String? = null
    private var latLng: LatLng? = null

    private val args: MapFragmentArgs by navArgs()
    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.etSearch.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_locationListFragment)
        }

        binding.btnChange.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_locationListFragment)
        }

        binding.btnCurrentLocation.setOnClickListener {
            viewModel.fetchUserLocation()
        }

        binding.btnSetLocation.setOnClickListener {

            val currentLatLng = latLng
            if (currentLatLng == null) {
                Toast.makeText(requireContext(), "Location not ready", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val action = MapFragmentDirections
                .actionMapFragmentToSaveAddressFragment(
                    latitude = currentLatLng.latitude.toString(),
                    longitude = currentLatLng.longitude.toString()
                )

            findNavController().navigate(action)
        }

        // 🔥 STATE OBSERVE (FINAL)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when (state) {

                        is MapState.Loading -> {
                            // optional loader
                        }

                        is MapState.LocationReceived -> {
                            val newLatLng = LatLng(state.lat, state.lng)
                            latLng = newLatLng
                            showLocationOnMap(newLatLng)
                        }

                        is MapState.AddressReceived -> {
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
                        }

                        is MapState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        placeId = args.placeId
        if (placeId != null) {
            fetchPlaceLatLng(placeId!!)
        } else {
            viewModel.fetchUserLocation()
        }
    }

    private fun fetchPlaceLatLng(placeId: String) {

        val request = FetchPlaceRequest.builder(
            placeId,
            listOf(Place.Field.LAT_LNG)
        ).build()

        val placesClient = Places.createClient(requireContext())

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->

                val resultLatLng = response.place.latLng

                if (resultLatLng != null) {
                    latLng = resultLatLng
                    showLocationOnMap(resultLatLng)

                    viewModel.getAddressFromLatLng(
                        resultLatLng.latitude,
                        resultLatLng.longitude
                    )
                }
            }
    }

    private fun showLocationOnMap(latLng: LatLng) {
        googleMap?.apply {
            clear()
            setPadding(0, 0, 0, 300)
            addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}