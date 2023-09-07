package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.PagingAndSortingRepository;
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
public interface BookingRepository extends Repository<Booking, Long>, PagingAndSortingRepository<Booking, Long> {
    Booking save(Booking booking);

    Optional<Booking> findByIdOrderByIdDesc(Long bookingId);

    Page<Booking> findByBookerIdOrderByIdDesc(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking>
    findByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(Long bookerId, LocalDateTime nowForStart,
                                                        LocalDateTime nowForEnd, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBeforeOrderByIdDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfterOrderByIdDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByIdDesc(Long bookerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusOrderByIdDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking>
    findByItemOwnerIdAndStartBeforeAndEndAfterOrderByIdAsc(Long bookerId, LocalDateTime nowForStart,
                                                           LocalDateTime nowForEnd, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndBeforeOrderByIdDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartAfterOrderByIdDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    Streamable<LastOrNextBooking> findByItemIdOrderByStartAsc(Long itemId);

    Optional<List<Booking>> findByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long booker,
                                                                         BookingStatus status, LocalDateTime now);

    long countByBookerId(long userId);
    long countByItemOwnerId(long userId);
}