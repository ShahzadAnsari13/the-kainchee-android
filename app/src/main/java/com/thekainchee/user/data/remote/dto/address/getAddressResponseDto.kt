package com.thekainchee.user.data.remote.dto.address

import com.thekainchee.user.domain.model.UserAddress

data class getAddressResponseDto(
    val message : String,
    val count : Int,
    val addresses : List<AddressResponseData>
    )
