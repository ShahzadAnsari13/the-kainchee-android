package com.thekainchee.user.data.repository

import com.thekainchee.user.data.mapper.toUiModel
import com.thekainchee.user.data.remote.api.NotificationApi
import com.thekainchee.user.domain.repository.NotificationRepository
import com.thekainchee.user.presentation.notification.model.NotificationUiModel
import com.thekainchee.user.utils.ErrorUtils
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
                val error = ErrorUtils.parseError(response.errorBody()?.string())
                Result.failure(
                    Exception(error.message ?: "Failed to fetch notifications")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}