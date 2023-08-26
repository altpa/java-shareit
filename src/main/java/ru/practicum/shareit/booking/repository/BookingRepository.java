package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastOrNextBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface BookingRepository extends Repository<Booking, Long> {
    Booking save(Booking booking);

    Optional<Booking> findByIdOrderByIdDesc(Long bookingId);

    Streamable<Booking> findByBookerIdOrderByIdDesc(Long bookerId);

    Streamable<Booking> findByBookerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status);

    Streamable<Booking>
    findByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(Long bookerId, LocalDateTime nowForStart,
                                                        LocalDateTime nowForEnd);

    Streamable<Booking> findByBookerIdAndEndBeforeOrderByIdDesc(Long bookerId, LocalDateTime now);

    Streamable<Booking> findByBookerIdAndStartAfterOrderByIdDesc(Long bookerId, LocalDateTime now);

    Streamable<Booking> findByItemOwnerIdOrderByIdDesc(Long bookerId);

    Streamable<Booking> findByItemOwnerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status);

    Streamable<Booking>
    findByItemOwnerIdAndStartBeforeAndEndAfterOrderByIdAsc(Long bookerId, LocalDateTime nowForStart,
                                                           LocalDateTime nowForEnd);

    Streamable<Booking> findByItemOwnerIdAndEndBeforeOrderByIdDesc(Long bookerId, LocalDateTime now);

    Streamable<Booking> findByItemOwnerIdAndStartAfterOrderByIdDesc(Long bookerId, LocalDateTime now);

    Streamable<LastOrNextBooking> findByItemIdOrderByStartAsc(Long itemId);

    Optional<List<Booking>> findByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long booker,
                                                                         BookingStatus status, LocalDateTime now);
}