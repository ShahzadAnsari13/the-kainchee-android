package com.thekainchee.user.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thekainchee.user.data.local.room.dao.SelectedServiceDao
import com.thekainchee.user.data.local.room.dao.UserAddressDao
import com.thekainchee.user.data.local.room.dao.UserDao
import com.thekainchee.user.data.local.room.entity.SelectedServiceEntity
import com.thekainchee.user.data.local.room.entity.UserAddressEntity
import com.thekainchee.user.data.local.room.entity.UserEntity

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