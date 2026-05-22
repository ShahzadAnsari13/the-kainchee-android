package com.thekainchee.user.presentation.booking.model

data class StaffUiModel(val id : String,
                        val name : String,
                        val image : String,
                        val phone : String,
                        val rating : RatingModel,
                        val experience : Int
    )