package com.alenear.user.data.remote.api

import com.alenear.user.data.remote.dto.Notification.NotificationResponse
import retrofit2.Response
import retrofit2.http.GET

interface NotificationApi {
    @GET("notification")
    suspend fun getNotifications(): Response<NotificationResponse>
}