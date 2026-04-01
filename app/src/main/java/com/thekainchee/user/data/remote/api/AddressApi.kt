package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.add_address.AddAddressResponseDto
import com.thekainchee.user.data.remote.dto.add_address.UserAddressDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AddressApi {
    @POST("user/addresses")
    suspend fun addAddress(
        @Body request: UserAddressDto
    ): AddAddressResponseDto
}