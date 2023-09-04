package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto, long userId);

    BookingDto changeStatus(Long bookingId, boolean approved, long userId);

    BookingDto getById(long bookingId, long userId);

    List<BookingDto> getByUserIdAndStateByBooker(long userId, String state, int from, int size);

    List<BookingDto> getByUserIdAndStateByOwner(long userId, String state, int from, int size);

    LastOrNextBooking getLastBooking(ItemDto item, long ownerId);

    LastOrNextBooking getNextBooking(ItemDto item, long ownerId);
}