package com.thekainchee.user.di

import android.content.Context
import androidx.room.Room
import com.thekainchee.user.data.local.room.dao.UserAddressDao
import com.thekainchee.user.data.local.room.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "the_kainchee_db"
        ).build()
    }

    @Provides
    fun provideUserAddressDao(
        database: AppDatabase
    ): UserAddressDao {

        return database.userAddressDao()
    }

}