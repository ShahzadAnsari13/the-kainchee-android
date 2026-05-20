package com.thekainchee.user.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thekainchee.user.data.local.room.entity.SelectedServiceEntity

@Dao
interface  SelectedServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedService(service: SelectedServiceEntity)

    @Query("DELETE FROM selected_services WHERE serviceId = :serviceId")
    suspend fun removeService(serviceId: String)

    @Query("SELECT * FROM selected_services")
    suspend fun getSelectedServices(): List<SelectedServiceEntity>

    @Query("DELETE FROM selected_services")
    suspend fun clearAllService()

}