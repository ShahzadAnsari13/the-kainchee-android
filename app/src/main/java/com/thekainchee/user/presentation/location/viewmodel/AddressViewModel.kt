package com.thekainchee.user.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.AddressRepository
import com.thekainchee.user.presentation.location.state.AddressDeleteEvent
import com.thekainchee.user.presentation.location.state.AddressListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddressViewModel @Inject constructor(private val addressRepository: AddressRepository) : ViewModel() {
    private val _state = MutableStateFlow<AddressListState>(AddressListState.Idle)
    val state : StateFlow<AddressListState> = _state

    private val _event = MutableSharedFlow<AddressDeleteEvent>()
    val event = _event
    private val _actionId = MutableStateFlow<String?>(null)
    val actionId: StateFlow<String?> = _actionId
    init {
        // 1. Refresh (one-time)
        viewModelScope.launch {
            _state.value = AddressListState.Loading
            try {
                addressRepository.refreshAddresses()
            } catch (e: Exception) {
                _state.value = AddressListState.Error(e.message ?: "Error")
            }
        }

        // 2. Collect (continuous)
        viewModelScope.launch {
            addressRepository.getAddresses().collect { list ->
                _state.value = AddressListState.Success(list)
            }
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            _actionId.value = id
            try {
                addressRepository.deleteAddress(id)
                addressRepository.refreshAddresses()

                _event.emit(AddressDeleteEvent.ShowMessage("Address deleted successfully"))

            } catch (e: Exception) {
                _event.emit(AddressDeleteEvent.ShowMessage("Something went wrong"))
            }
            finally {
                _actionId.value = null
            }
        }
    }

    fun setDefaultAddress(id: String) {
        viewModelScope.launch {
            _actionId.value = id
            try {
                addressRepository.setDefaultAddress(id)
                addressRepository.refreshAddresses()
                _event.emit(AddressDeleteEvent.ShowMessage("Address set as default successfully"))
            } catch (e: Exception) {
                _event.emit(AddressDeleteEvent.ShowMessage(e.message ?: "Something went wrong"))
            } finally {
                _actionId.value = null
            }
        }
    }
}