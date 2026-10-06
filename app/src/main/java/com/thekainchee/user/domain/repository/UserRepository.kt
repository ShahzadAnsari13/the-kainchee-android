package com.thekainchee.user.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserName(): Flow<String?>
}