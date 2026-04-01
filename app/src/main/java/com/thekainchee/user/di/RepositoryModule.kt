package com.thekainchee.user.di

import com.thekainchee.user.data.repository.AddressRepositoryImpl
import com.thekainchee.user.data.repository.AuthRepositoryImpl
import com.thekainchee.user.data.repository.DeviceLocationRepositoryImpl
import com.thekainchee.user.data.repository.PlaceSearchRepositoryImpl
import com.thekainchee.user.domain.repository.AddressRepository
import com.thekainchee.user.domain.repository.AuthRepository
import com.thekainchee.user.domain.repository.DeviceLocationRepository
import com.thekainchee.user.domain.repository.PlaceSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: DeviceLocationRepositoryImpl
    ): DeviceLocationRepository

    @Binds
    @Singleton
    abstract fun bindPlaceSearchRepository(
        placeSearchRepositoryImpl: PlaceSearchRepositoryImpl
    ): PlaceSearchRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(
        addressRepositoryImpl: AddressRepositoryImpl
    ): AddressRepository
}