package com.alenear.user.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserName(): Flow<String?>
}