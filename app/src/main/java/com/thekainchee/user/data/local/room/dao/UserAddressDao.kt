package com.thekainchee.user.data.local.room.dao

import androidx.room.*
import com.thekainchee.user.data.local.room.entity.UserAddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAddressDao {

    //  Get All (UI observe karega)
    @Query("SELECT * FROM user_address")
    fun getAllAddresses(): Flow<List<UserAddressEntity>>

    //  Get by ID (selected ke liye)
    @Query("SELECT * FROM user_address WHERE id = :id LIMIT 1")
    suspend fun getAddressById(id: String): UserAddressEntity?

    //  Default address
    @Query("SELECT * FROM user_address WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAddress(): UserAddressEntity?

    //  Any address (fallback)
    @Query("SELECT * FROM user_address LIMIT 1")
    suspend fun getAnyAddress(): UserAddressEntity?

    //  Insert / Update (UPSERT)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: UserAddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(addresses: List<UserAddressEntity>)

    //  Update Default (ONLY ONE TRUE)
    @Query("""
        UPDATE user_address 
        SET isDefault = CASE 
            WHEN id = :id THEN 1 
            ELSE 0 
        END
    """)
    suspend fun updateDefault(id: String)

    //  Delete specific address
    @Query("DELETE FROM user_address WHERE id = :id")
    suspend fun deleteById(id: String)

    //  Clear all (rarely use, but keep for safety)
    @Query("DELETE FROM user_address")
    suspend fun clearAll()
}