package com.thekainchee.user.presentation.dashboard.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.DeviceLocationRepository
import com.thekainchee.user.presentation.dashboard.home.state.LocationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepository: DeviceLocationRepository) : ViewModel() {
    private val _location = MutableStateFlow<LocationUiState>(LocationUiState.Idle)
    val location : StateFlow<LocationUiState> = _location

    fun fetchUserLocation(){
        viewModelScope.launch {
            _location.value = LocationUiState.Loading
            try {
                val result = locationRepository.getUserLocation()

                _location.value = LocationUiState.Success(result)

            }catch(e : Exception){
                _location.value = LocationUiState.Error(e.message ?: "Unable to load location")
            }
        }
    }

}