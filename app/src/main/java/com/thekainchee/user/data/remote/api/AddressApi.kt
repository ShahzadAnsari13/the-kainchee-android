package com.thekainchee.user.data.remote.api

import com.thekainchee.user.data.remote.dto.address.AddAddressResponseDto
import com.thekainchee.user.data.remote.dto.address.DeleteResponseDto
import com.thekainchee.user.data.remote.dto.address.SetDefaultAddressResponseDTO
import com.thekainchee.user.data.remote.dto.address.UpdateAddressResponseDto
import com.thekainchee.user.data.remote.dto.address.UserAddressDto
import com.thekainchee.user.data.remote.dto.address.getAddressResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressApi {
    @POST("user/addresses")
    suspend fun addAddress(
        @Body request: UserAddressDto
    ): Response<AddAddressResponseDto>

    @GET("user/addresses")
    suspend fun getAddresses(): Response<getAddressResponseDto>

    @PUT("user/addresses/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: String,
        @Body request: UserAddressDto
    ): Response<UpdateAddressResponseDto>

    @DELETE("user/addresses/{addressId}")
    suspend fun deleteAddress(
        @Path("addressId") addressId:String
    ): Response<DeleteResponseDto>

    @PATCH("user/addresses/{addressId}")
    suspend fun setDefaultAddress(
        @Path("addressId") addressId:String
    ): Response<SetDefaultAddressResponseDTO>

}