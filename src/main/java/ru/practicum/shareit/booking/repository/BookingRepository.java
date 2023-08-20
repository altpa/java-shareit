package ru.practicum.shareit.booking.repository;

import org.springframework.data.repository.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

public interface BookingRepository extends Repository<Booking, Long> {
    Booking save(Booking booking);

}
