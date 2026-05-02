package com.thekainchee.user.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.DeviceLocationRepository
import com.thekainchee.user.presentation.location.state.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val locationRepository: DeviceLocationRepository) : ViewModel()  {
    private val _state = MutableStateFlow<MapState>(MapState.Idle)
    val state: StateFlow<MapState> = _state

    fun fetchUserLocation() {
        viewModelScope.launch {
            _state.value = MapState.Loading
            try {
                val location = locationRepository.getUserLocation()

                _state.value = MapState.LocationReceived(
                    lat = location.latitude,
                    lng = location.longitude
                )

                // automatically fetch address
                getAddressFromLatLng(location.latitude, location.longitude)

            } catch (e: Exception) {
                _state.value = MapState.Error(e.message ?: "Location error")
            }
        }
    }
    fun getAddressFromLatLng(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val address = locationRepository.getAddressFromLatLng(lat, lng)
                _state.value = MapState.AddressReceived(lat,lng,address)
            } catch (e: Exception) {
                _state.value = MapState.Error(e.message ?: "Address error")
            }
        }
    }
}