package com.alenear.user.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import com.alenear.user.domain.model.AddressMode
import com.alenear.user.presentation.location.model.AddressUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AddressSharedViewModel @Inject constructor() : ViewModel() {
    var mode : AddressMode = AddressMode.ADD
    var selectedAddress : AddressUI? = null
}