package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto, long userId);

    List<BookingDto> getAllBookings(long bookingId);

//    BookingDto updateBooking(BookingDto bookingDto, status);
//    BookingDto updateBooking(BookingDto bookingDto, approved);

    BookingDto getBookingById(long bookingId);

}
