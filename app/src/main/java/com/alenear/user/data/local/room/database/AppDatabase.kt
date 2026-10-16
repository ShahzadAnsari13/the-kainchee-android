package com.alenear.user.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alenear.user.data.local.room.dao.SelectedServiceDao
import com.alenear.user.data.local.room.dao.UserAddressDao
import com.alenear.user.data.local.room.dao.UserDao
import com.alenear.user.data.local.room.entity.SelectedServiceEntity
import com.alenear.user.data.local.room.entity.UserAddressEntity
import com.alenear.user.data.local.room.entity.UserEntity

@Database(
    entities = [UserAddressEntity::class, SelectedServiceEntity::class,
        UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userAddressDao(): UserAddressDao

    abstract fun selectedServiceDao(): SelectedServiceDao

    abstract fun userDao(): UserDao
}