package com.alenear.user.data.repository

import com.alenear.user.data.local.room.dao.UserDao
import com.alenear.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getUserName(): Flow<String?> {
        return userDao.getUserName()
    }
}