package com.thekainchee.user.data.mapper

import com.thekainchee.user.data.remote.dto.booking.BookingDetailDto
import com.thekainchee.user.data.remote.dto.booking.BookingDetailResponseDto
import com.thekainchee.user.data.remote.dto.booking.BookingDetailsServiceDto
import com.thekainchee.user.data.remote.dto.booking.BookingRequestDto
import com.thekainchee.user.data.remote.dto.booking.BookingResponseDto
import com.thekainchee.user.data.remote.dto.booking.StaffDto
import com.thekainchee.user.data.remote.dto.parlour.RatingDto
import com.thekainchee.user.presentation.booking.model.BookingDetailUiModel
import com.thekainchee.user.presentation.booking.model.BookingServiceUiModel
import com.thekainchee.user.presentation.booking.model.BookingUiModel
import com.thekainchee.user.presentation.booking.model.CreateBookingParams
import com.thekainchee.user.presentation.booking.model.RatingModel
import com.thekainchee.user.presentation.booking.model.StaffUiModel

fun StaffDto.toUi() : StaffUiModel{
    return StaffUiModel(
        id = id,
        name = name,
        image = profileImage,
        phone = phone,
        rating = rating.toUi(),
        experience = experienceYears
    )
}

fun RatingDto.toUi(): RatingModel {
    return RatingModel(
        average = average.toFloat(),
        count = count
    )
}


fun CreateBookingParams.toBookingRequestDto(): BookingRequestDto {
    return BookingRequestDto(
        parlourId = parlourId,
        staffId = staffId,
        bookingDate = bookingDate,
        serviceIds = serviceIds,
        slotStartTime = slotStartTime
    )
}

fun BookingResponseDto.toBookingUiModel(): BookingUiModel{
    return BookingUiModel(
        message = message,
        bookingId = bookingId
    )

}
fun BookingDetailDto.toUiModel(): BookingDetailUiModel {
    return BookingDetailUiModel(
        bookingId = _id,
        parlourName = parlourId.name,
        parlourPhone = parlourId.contactNumber,
        staffName = staffId.name,
        services = services.map { it.toUiModel() },
        totalPrice = totalPrice,
        bookingDate = bookingDate,
        slotStartTime = slotStartTime,
        slotEndTime = slotEndTime,
        paymentMethod = paymentMethod,
        paymentStatus = paymentStatus,
        bookingStatus = bookingStatus,
        location = parlourId.location,
        createdAt = createdAt
    )
}
fun BookingDetailsServiceDto.toUiModel(): BookingServiceUiModel {
    return BookingServiceUiModel(
        serviceId = serviceId,
        name = name,
        price = price,
        durationMinutes = durationMinutes,
        image = image
    )
}