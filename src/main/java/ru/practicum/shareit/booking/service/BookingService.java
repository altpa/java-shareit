package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingDto bookingDto, long userId);

    Booking changeStatus(Long bookingId, boolean approved, long userId);

    Booking getById(long bookingId, long userId);

    List<Booking> getByUserIdAndStateByBooker(long userId, String state);

    List<Booking> getByUserIdAndStateByOwner(long userId, String state);

    LastOrNextBooking getLastBooking(ItemDto item, long ownerId);

    LastOrNextBooking getNextBooking(ItemDto item, long ownerId);
}