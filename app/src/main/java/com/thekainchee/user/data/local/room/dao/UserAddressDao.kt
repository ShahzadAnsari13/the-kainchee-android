package com.thekainchee.user.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thekainchee.user.data.local.room.entity.UserAddressEntity

@Dao
interface UserAddressDao {

    @Query("SELECT * FROM user_address WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAddress(): UserAddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: UserAddressEntity)

    @Update
    suspend fun updateAddress(address: UserAddressEntity)

    @Query("DELETE FROM user_address WHERE label = 'CurrentLocation'")
    suspend fun deleteLiveLocation()

}