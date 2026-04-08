package com.thekainchee.user.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.AddressRepository
import com.thekainchee.user.presentation.location.state.AddressState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveUpdateAddressViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AddressState>(AddressState.Idle)
    val state: StateFlow<AddressState> = _state

    fun saveAddress(address: UserAddress) {
        viewModelScope.launch {
            _state.value = AddressState.Loading

            try {
                val result = repository.addAddress(address)

                _state.value = AddressState.CreateAddress(
                    message = "Address added successfully",
                    address = result
                )

            } catch (e: Exception) {
                _state.value = AddressState.Error(
                    message = e.message ?: "Something went wrong"
                )
            }
        }
    }
    fun updateAddress(id: String?, address: UserAddress) {
        viewModelScope.launch {
            _state.value = AddressState.Loading

            try {
                id?.let {
                    repository.updateAddress(it, address)
                    _state.value = AddressState.UpdateAddress("Address updated successfully")
                } ?: run {
                    _state.value = AddressState.Error("Invalid address")
                }

            } catch (e: Exception) {
                _state.value = AddressState.Error("Something went wrong")
            }
        }
    }


}