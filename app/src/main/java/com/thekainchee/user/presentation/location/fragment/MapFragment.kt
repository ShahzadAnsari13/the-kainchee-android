package com.thekainchee.user.presentation.location.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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

class MapFragment : Fragment() , OnMapReadyCallback{
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private var placeId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.etSearch.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_locationListFragment)
        }

        placeId = arguments?.getString("placeId")

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if(placeId!=null){
            fetchPlaceLatLng(placeId!!)
        }else{

        }
    }
    private fun fetchPlaceLatLng(placeId: String) {

        val placeFields = listOf(Place.Field.LAT_LNG)

        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        val placesClient = Places.createClient(requireContext())

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val latLng = response.place.latLng

                if (latLng != null) {
                    showLocationOnMap(latLng)
                }
            }
    }

    private fun showLocationOnMap(latLng: LatLng) {

        googleMap.clear()

        googleMap.addMarker(
            MarkerOptions().position(latLng)
        )

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        )
    }


}