package com.thekainchee.user.data.remote.dto.profile

import java.util.Date

data class ProfileDto(val name  :String,
                      val countryCode : String,
                      val phoneNumber : String,
                      val walletBalance : Double,
                      val notificationsEnabled : Boolean,
                      val isActive : Boolean,
                      val memberSince : String)
