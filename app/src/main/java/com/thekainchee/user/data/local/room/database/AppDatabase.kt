package com.thekainchee.user.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thekainchee.user.data.local.room.dao.UserAddressDao
import com.thekainchee.user.data.local.room.entity.UserAddressEntity

@Database(
    entities = [UserAddressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userAddressDao(): UserAddressDao

}