package com.thekainchee.user.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import com.thekainchee.user.domain.model.AddressMode
import com.thekainchee.user.domain.model.UserAddress
import com.thekainchee.user.presentation.location.model.AddressUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AddressSharedViewModel @Inject constructor() : ViewModel() {
    var mode : AddressMode = AddressMode.ADD
    var selectedAddress : AddressUI? = null
}