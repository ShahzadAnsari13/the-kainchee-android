package com.thekainchee.user.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.domain.repository.AddressRepository
import com.thekainchee.user.presentation.location.state.AddressEvent
import com.thekainchee.user.presentation.location.state.AddressState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private val _event = MutableSharedFlow<AddressEvent>()
    val event = _event
    fun saveAddress(address: UserAddress) {
        viewModelScope.launch {
            _state.value = AddressState.Loading

            try {
                repository.addAddress(address)
                _event.emit(AddressEvent.NavigateBack("Address added successfully"))

            } catch (e: Exception) {
                _event.emit(
                    AddressEvent.ShowMessage(e.message ?: "Something went wrong")
                )
            }
            finally {
                _state.value = AddressState.Idle
            }
        }
    }
    fun updateAddress(id: String?, address: UserAddress) {
        viewModelScope.launch {
            _state.value = AddressState.Loading

            try {
                id?.let { repository.updateAddress(it, address)
                    _event.emit(AddressEvent.NavigateBack("Address updated successfully")) }
                    ?: run { _event.emit(AddressEvent.ShowMessage("Invalid Address")) }


            } catch (e: Exception) {
                _event.emit(
                    AddressEvent.ShowMessage(e.message ?: "Something went wrong")
                )
            }
            finally {
                _state.value = AddressState.Idle
            }
        }
    }


}