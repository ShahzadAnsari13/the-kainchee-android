package com.thekainchee.user.di

import com.thekainchee.user.data.repository.AddressRepositoryImpl
import com.thekainchee.user.data.repository.AuthRepositoryImpl
import com.thekainchee.user.data.repository.BookingRepositoryImpl
import com.thekainchee.user.data.repository.DeviceLocationRepositoryImpl
import com.thekainchee.user.data.repository.NotificationRepositoryImpl
import com.thekainchee.user.data.repository.ParlourRepositoryImpl
import com.thekainchee.user.data.repository.PaymentRepositoryImpl
import com.thekainchee.user.data.repository.PlaceSearchRepositoryImpl
import com.thekainchee.user.data.repository.ProfileRepositoryImpl
import com.thekainchee.user.data.repository.ServiceRepositoryImpl
import com.thekainchee.user.data.repository.WalletRepositoryImpl
import com.thekainchee.user.domain.repository.AddressRepository
import com.thekainchee.user.domain.repository.AuthRepository
import com.thekainchee.user.domain.repository.BookingRepository
import com.thekainchee.user.domain.repository.DeviceLocationRepository
import com.thekainchee.user.domain.repository.NotificationRepository
import com.thekainchee.user.domain.repository.ParlourRepository
import com.thekainchee.user.domain.repository.PaymentRepository
import com.thekainchee.user.domain.repository.PlaceSearchRepository
import com.thekainchee.user.domain.repository.ProfileRepository
import com.thekainchee.user.domain.repository.ServiceRepository
import com.thekainchee.user.domain.repository.WalletRepository
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

    @Binds
    @Singleton
    abstract  fun bindParlourRepository(
        parlourRepositoryImpl: ParlourRepositoryImpl
    ): ParlourRepository

    @Binds
    @Singleton
    abstract  fun bindServiceRepository(
        serviceRepositoryImpl: ServiceRepositoryImpl
    ): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ) : BookingRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: WalletRepositoryImpl
    ) : WalletRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ) : PaymentRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository
}