package com.thekainchee.user.data.repository

import com.thekainchee.user.data.local.room.dao.UserDao
import com.thekainchee.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getUserName(): Flow<String?> {
        return userDao.getUserName()
    }
}