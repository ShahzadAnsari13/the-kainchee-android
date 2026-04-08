package com.thekainchee.user.domain.model

data class UserAddress(
    val id: String?,
    val label : String,

    val latitude : Double,
    val longitude : Double,

    val country : String,
    val state : String,
    val district : String,
    val city : String?,
    val pincode : String?,

    val landmark : String?,
    val details : String?,

    val isDefault : Boolean
)
