package com.thekainchee.user.data.remote.dto.booking

import com.google.gson.annotations.SerializedName
import com.thekainchee.user.data.remote.dto.parlour.RatingDto

data class StaffDto(
    @SerializedName("_id")
    val id: String,
    val name : String,
    val profileImage : String,
    val phone :String,
    val rating : RatingDto,
    val experienceYears : Int) {

}
