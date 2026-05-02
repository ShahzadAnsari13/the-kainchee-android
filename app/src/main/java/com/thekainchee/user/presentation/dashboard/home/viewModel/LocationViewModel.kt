package com.thekainchee.user.presentation.dashboard.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.DeviceLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepository: DeviceLocationRepository) : ViewModel() {
    private val _location = MutableStateFlow<UserAddress?>(null)
    val location : StateFlow<UserAddress?> = _location

    fun fetchUserLocation(){
        viewModelScope.launch {
            try {
                val result = locationRepository.getUserLocation()

                _location.value = result

            }catch(e : Exception){
                e.printStackTrace()
            }
        }
    }

}