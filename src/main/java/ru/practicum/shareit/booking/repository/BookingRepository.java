package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.Streamable;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.Optional;

@EnableJpaRepositories
public interface BookingRepository extends Repository<Booking, Long> {
    Booking save(Booking booking);

    Optional<Booking> findByIdOrderByIdDesc(Long bookingId);

    Streamable<Booking> findByBookerIdOrderByIdDesc(Long bookerId);

    Streamable<Booking> findByBookerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status);

    Streamable<Booking> findByBookerIdAndEndAfterOrderByIdDesc(Long bookerId, Instant now);

    Streamable<Booking> findByBookerIdAndEndBeforeOrderByIdDesc(Long bookerId, Instant now);

    Streamable<Booking> findByBookerIdAndStartAfterOrderByIdDesc(Long bookerId, Instant now);

    Streamable<Booking> findByItemOwnerIdOrderByIdDesc(Long bookerId);

    Streamable<Booking> findByItemOwnerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status);

    Streamable<Booking> findByItemOwnerIdAndEndAfterOrderByIdDesc(Long bookerId, Instant now);

    Streamable<Booking> findByItemOwnerIdAndEndBeforeOrderByIdDesc(Long bookerId, Instant now);

    Streamable<Booking> findByItemOwnerIdAndStartAfterOrderByIdDesc(Long bookerId, Instant now);

    Streamable<Booking> findFirst2ByItemIdOrderByStartAsc(Long itemId);
}