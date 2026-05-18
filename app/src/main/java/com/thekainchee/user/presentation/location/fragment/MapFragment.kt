package com.thekainchee.user.presentation.location.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.domain.model.AddressMode
import com.thekainchee.user.presentation.location.state.MapState
import com.thekainchee.user.presentation.location.viewmodel.AddressSharedViewModel
import com.thekainchee.user.presentation.location.viewmodel.MapViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null

    private var latLng: LatLng? = null

    private val args: MapFragmentArgs by navArgs()
    private val viewModel: MapViewModel by viewModels()
    private var cameraJob: Job? = null
    private val addressSharedViewModel: AddressSharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.layoutNoInternet.visibility = View.VISIBLE
            binding.mainContent.visibility = View.GONE
        }else{
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }

        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root,"No Internet Connection",Snackbar.LENGTH_SHORT).show()
            }
            else{
                binding.layoutNoInternet.visibility = View.GONE
                binding.mainContent.visibility = View.VISIBLE
                if(googleMap == null){
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
                    mapFragment?.getMapAsync(this)
                }
            }
        }


        binding.etSearch.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutNoInternet.visibility = View.VISIBLE
                binding.mainContent.visibility = View.GONE
            }else{
                findNavController().popBackStack()
            }

        }

        binding.btnChange.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutNoInternet.visibility = View.VISIBLE
                binding.mainContent.visibility = View.GONE
            }else{
                findNavController().popBackStack()
            }


        }

        binding.btnCurrentLocation.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutNoInternet.visibility = View.VISIBLE
                binding.mainContent.visibility = View.GONE
            }else{
                viewModel.fetchUserLocation()
            }

        }

        binding.btnSetLocation.setOnClickListener {

            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutNoInternet.visibility = View.VISIBLE
                binding.mainContent.visibility = View.GONE
            }else{
                binding.layoutNoInternet.visibility = View.GONE
                binding.mainContent.visibility = View.VISIBLE
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

        }

        // STATE OBSERVE (FINAL)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when (state) {

                        is MapState.Loading -> {

                        }

                        is MapState.LocationReceived -> {
                            val newLatLng = LatLng(state.lat, state.lng)
                            latLng = newLatLng
                            showLocationOnMap(newLatLng)
                        }

                        is MapState.AddressReceived -> {
                            if (binding.locationCard.visibility != View.VISIBLE) {
                                binding.locationCard.visibility = View.VISIBLE
                                binding.locationCard.alpha = 0f
                                binding.locationCard.animate().alpha(1f).setDuration(200).start()
                            }
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
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        when (addressSharedViewModel.mode) {

            AddressMode.EDIT -> {

                val selectedAddress = addressSharedViewModel.selectedAddress ?: return
                binding.btnSetLocation.text = "Update Location"
                latLng = LatLng(selectedAddress.latitude, selectedAddress.longitude)

                viewModel.getAddressFromLatLng(
                    selectedAddress.latitude,
                    selectedAddress.longitude
                )

                latLng?.let { showLocationOnMap(it) }
            }

            AddressMode.ADD -> {

                val placeId = args.placeId

                placeId?.let {
                    fetchPlaceLatLng(it)
                } ?: viewModel.fetchUserLocation()
            }
        }
        googleMap?.setOnCameraMoveStartedListener {
            if (binding.locationCard.visibility != View.VISIBLE) {
                binding.locationCard.visibility = View.VISIBLE
                binding.locationCard.alpha = 0f
                binding.locationCard.animate().alpha(1f).setDuration(200).start()
            }
        }
        googleMap?.setOnCameraIdleListener {
            
            val centerLatLng = googleMap?.cameraPosition?.target ?: return@setOnCameraIdleListener

            latLng = centerLatLng
            cameraJob?.cancel()

            cameraJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)

                viewModel.getAddressFromLatLng(
                    centerLatLng.latitude,
                    centerLatLng.longitude
                )
            }
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
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch location",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showLocationOnMap(latLng: LatLng) {
        googleMap?.apply {
            setPadding(0, 0, 0, 300)
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraJob?.cancel()
        _binding = null
    }
}