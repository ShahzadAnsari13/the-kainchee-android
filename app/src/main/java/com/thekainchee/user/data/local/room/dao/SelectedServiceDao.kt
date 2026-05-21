package com.thekainchee.user.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thekainchee.user.data.local.room.entity.SelectedServiceEntity

@Dao
interface  SelectedServiceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSelectedService(service: SelectedServiceEntity)

    @Query("DELETE FROM selected_services WHERE parlourId = :parlourId AND serviceId = :serviceId ")
    suspend fun removeService(parlourId: String, serviceId: String)

    @Query("SELECT * FROM selected_services WHERE parlourId = :parlourId")
    suspend fun getSelectedServices(parlourId: String): List<SelectedServiceEntity>

    @Query("DELETE FROM selected_services WHERE parlourId = :parlourId")
    suspend fun clearAllService(parlourId: String)

}