package com.alenear.user.di

import com.alenear.user.data.repository.AddressRepositoryImpl
import com.alenear.user.data.repository.AuthRepositoryImpl
import com.alenear.user.data.repository.BookingRepositoryImpl
import com.alenear.user.data.repository.DeviceLocationRepositoryImpl
import com.alenear.user.data.repository.NotificationRepositoryImpl
import com.alenear.user.data.repository.ParlourRepositoryImpl
import com.alenear.user.data.repository.PaymentRepositoryImpl
import com.alenear.user.data.repository.PlaceSearchRepositoryImpl
import com.alenear.user.data.repository.ProfileRepositoryImpl
import com.alenear.user.data.repository.ReferralRepositoryImpl
import com.alenear.user.data.repository.SearchRepositoryImpl
import com.alenear.user.data.repository.ServiceRepositoryImpl
import com.alenear.user.data.repository.UserRepositoryImpl
import com.alenear.user.data.repository.WalletRepositoryImpl
import com.alenear.user.domain.repository.AddressRepository
import com.alenear.user.domain.repository.AuthRepository
import com.alenear.user.domain.repository.BookingRepository
import com.alenear.user.domain.repository.DeviceLocationRepository
import com.alenear.user.domain.repository.NotificationRepository
import com.alenear.user.domain.repository.ParlourRepository
import com.alenear.user.domain.repository.PaymentRepository
import com.alenear.user.domain.repository.PlaceSearchRepository
import com.alenear.user.domain.repository.ProfileRepository
import com.alenear.user.domain.repository.ReferralRepository
import com.alenear.user.domain.repository.SearchRepository
import com.alenear.user.domain.repository.ServiceRepository
import com.alenear.user.domain.repository.UserRepository
import com.alenear.user.domain.repository.WalletRepository
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

    @Binds
    @Singleton
    abstract fun bindReferralRepository(
        impl: ReferralRepositoryImpl
    ): ReferralRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl : SearchRepositoryImpl
    ): SearchRepository
}