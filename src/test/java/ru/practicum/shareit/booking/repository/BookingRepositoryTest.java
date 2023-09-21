package ru.practicum.shareit.booking.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;


@DataJpaTest
class BookingRepositoryTest {
    private final EasyRandom generator = new EasyRandom();
    private final Pageable page =  PageRequest.of(0, 10);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Booking actualBooking1;
    private Booking actualBooking2;
    private Item actualItem;
    private User actualUser1;
    private User actualUser2;

    @BeforeEach
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void setUp() {
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);

        actualUser1 = userRepository.save(generator.nextObject(User.class));
        actualUser2 = userRepository.save(generator.nextObject(User.class));

        Item item = generator.nextObject(Item.class);
        item.setOwner(actualUser2);
        actualItem = itemRepository.save(item);

        booking1.setBooker(actualUser1);
        booking1.setItem(actualItem);
        booking1.setStatus(WAITING);
        booking2.setBooker(actualUser2);
        booking2.setItem(actualItem);
        booking2.setStatus(APPROVED);

        actualBooking1 = bookingRepository.save(booking1);
        actualBooking2 = bookingRepository.save(booking2);
    }

    @Test
    void saveTest() {
        Booking booking3 = generator.nextObject(Booking.class);
        booking3.setBooker(actualUser1);
        booking3.setItem(actualItem);
        Booking actualBooking3 = bookingRepository.save(booking3);

        assertEquals(3, bookingRepository.count());
    }

    @Test
    void saveTestWhenUpdate() {
        actualBooking1.setStatus(APPROVED);
        LocalDateTime now = LocalDateTime.now();
        actualBooking1.setStart(now.plusDays(1));
        actualBooking1.setEnd(now.plusDays(2));

        bookingRepository.save(actualBooking1);
        assertEquals(actualBooking1, bookingRepository.findById(actualBooking1.getId()).get());
    }

    @Test
    void findByIdOrderByIdDesc() {
        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByIdOrderByIdDesc(actualBooking1.getId()).get().getId());
    }

    @Test
    void findByBookerIdOrderByIdDesc() {
        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByBookerIdOrderByIdDesc(actualUser1.getId(), page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByBookerIdAndStatusOrderByIdDesc() {
        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByBookerIdAndStatusOrderByIdDesc(actualBooking1.getBooker().getId(), WAITING, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc() {
        actualBooking1.setStart(LocalDateTime.now().minusDays(1));
        actualBooking1.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(actualBooking1);
        LocalDateTime now = LocalDateTime.now();

        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(
                        actualUser1.getId(), now, now, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByIdDesc() {
        actualBooking1.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(actualBooking1);
        LocalDateTime now = LocalDateTime.now();

        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByBookerIdAndEndBeforeOrderByIdDesc(
                                actualUser1.getId(), now, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByBookerIdAndStartAfterOrderByIdDesc() {
        actualBooking1.setStart(LocalDateTime.now().plusDays(1));
        bookingRepository.save(actualBooking1);
        LocalDateTime now = LocalDateTime.now();

        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByBookerIdAndStartAfterOrderByIdDesc(
                                actualUser1.getId(), now, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByItemOwnerIdOrderByIdDesc() {
        assertEquals(
                List.of(actualBooking2, actualBooking1),
                bookingRepository.findByItemOwnerIdOrderByIdDesc(
                        actualItem.getOwner().getId(), page)
                        .get().collect(Collectors.toList()));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByIdDesc() {
        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(
                                actualItem.getOwner().getId(), WAITING, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfterOrderByIdAsc() {
        actualBooking1.setStart(LocalDateTime.now().minusDays(1));
        actualBooking1.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(actualBooking1);
        LocalDateTime now = LocalDateTime.now();

        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByIdAsc(
                                actualItem.getOwner().getId(), now, now, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByItemOwnerIdAndEndBeforeOrderByIdDesc() {
        actualBooking1.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(actualBooking1);
        LocalDateTime now = LocalDateTime.now();

        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByItemOwnerIdAndEndBeforeOrderByIdDesc(
                                actualItem.getOwner().getId(), now, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByItemOwnerIdAndStartAfterOrderByIdDesc() {
        actualBooking1.setStart(LocalDateTime.now().plusDays(1));
        bookingRepository.save(actualBooking1);
        LocalDateTime now = LocalDateTime.now();

        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByItemOwnerIdAndEndBeforeOrderByIdDesc(
                                actualItem.getOwner().getId(), now, page)
                        .get().collect(Collectors.toList()).get(0).getId());
    }

    @Test
    void findByItemIdOrderByStartAsc() {
        actualBooking1.setStart(LocalDateTime.now().plusDays(1));
        bookingRepository.save(actualBooking1);
        actualBooking2.setStart(LocalDateTime.now().plusDays(2));
        bookingRepository.save(actualBooking2);

        assertEquals(
                List.of(actualBooking1.getId(), actualBooking2.getId()),
                bookingRepository.findByItemIdOrderByStartAsc(actualItem.getId())
                        .stream().map(LastOrNextBooking::getId).collect(Collectors.toList()));
    }

    @Test
    void findByItemIdAndBookerIdAndStatusAndEndBefore() {
        actualBooking1.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(actualBooking1);
        LocalDateTime now = LocalDateTime.now();

        assertEquals(
                actualBooking1.getId(),
                bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(actualItem.getId(),
                        actualUser1.getId(), WAITING, now).get().get(0).getId());
    }

    @Test
    void countByBookerId() {
        assertEquals(1, bookingRepository.countByBookerId(actualUser1.getId()));
    }

    @Test
    void countByItemOwnerId() {
        assertEquals(2, bookingRepository.countByItemOwnerId(actualItem.getOwner().getId()));
    }
}