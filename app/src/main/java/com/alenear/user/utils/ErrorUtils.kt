package com.alenear.user.utils

import com.alenear.user.presentation.common.model.ApiError
import org.json.JSONObject

object ErrorUtils {
    fun parseError(errorBody: String?): ApiError {
        return try {
            val json = JSONObject(errorBody ?: "")
            ApiError(
                message = json.optString("message", "Something went wrong"),
                code = json.optString("code", null)
            )
        } catch (e: Exception) {
            ApiError("Something went wrong")
        }
    }
}