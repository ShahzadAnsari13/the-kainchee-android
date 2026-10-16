package com.alenear.user.data.repository

import com.alenear.user.data.mapper.toUiModel
import com.alenear.user.data.remote.api.NotificationApi
import com.alenear.user.domain.repository.NotificationRepository
import com.alenear.user.presentation.notification.model.NotificationUiModel
import com.alenear.user.utils.ErrorUtils
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApi
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<NotificationUiModel>> {
        return try {
            val response = api.getNotifications()

            if (response.isSuccessful) {
                Result.success(
                    response.body()?.data?.map { it.toUiModel() } ?: emptyList()
                )
            } else {

                val errorBody = response.errorBody()?.string()
                val error = ErrorUtils.parseError(errorBody)
                Result.failure(
                    Exception(error.message ?: "Failed to fetch notifications")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}