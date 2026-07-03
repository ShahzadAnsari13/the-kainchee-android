package com.thekainchee.user.data.remote.dto.Notification

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("_id")
    val id: String,

    val title: String,

    val body: String,

    val type: String,

    val isRead: Boolean,

    val createdAt: String
)