package com.alenear.user.data.remote.dto.address

data class getAddressResponseDto(
    val message : String,
    val count : Int,
    val addresses : List<AddressResponseData>
    )
