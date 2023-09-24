package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@SpringBootTest
class BookingServiceImplIntegrationTest {
    private static final EasyRandom generator = new EasyRandom();
    private static final BookingMapper bookingMapper = BookingMapper.INSTANCE;
    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private static final ItemMapper itemMapper = ItemMapper.INSTANCE;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemServiceImpl itemService;

    private UserDto actualUserDto1;
    private UserDto actualUserDto2;
    private BookingDto createdBookingDto1;

    @BeforeEach
    public void setUp() {
        User createdUser1 = generator.nextObject(User.class);
        User createdUser2 = generator.nextObject(User.class);
        actualUserDto1 = userService.addUser(userMapper.userToUserDto(createdUser1));
        actualUserDto2 = userService.addUser(userMapper.userToUserDto(createdUser2));

        Item item1 = generator.nextObject(Item.class);
        item1.setAvailable(true);
        ItemDto itemDto1 = itemService
                .addItem(itemMapper.itemToItemDto(item1), actualUserDto1.getId(),
                        userService.checkOwner(actualUserDto1.getId()));
        itemDto1.setOwner(userMapper.userDtoToUser(actualUserDto1));

        Booking createdBooking1 = generator.nextObject(Booking.class);
        createdBookingDto1 = bookingMapper.bookingToBookingDto(createdBooking1);
        createdBookingDto1.setItem(itemMapper.itemDtoToItem(itemDto1));
        createdBookingDto1.setStart(LocalDateTime.now().plusDays(1));
        createdBookingDto1.setEnd(LocalDateTime.now().plusDays(2));
        createdBookingDto1.setItemId(itemDto1.getId());
    }

    @Test
    @DirtiesContext
    void addBooking() {
        BookingDto bookingDto =
                bookingService.addBooking(createdBookingDto1, actualUserDto2.getId());

        assertEquals(1, bookingDto.getId());
    }

    @Test
    @DirtiesContext
    void changeStatus() {
        createdBookingDto1.setStatus(WAITING);

        BookingDto bookingDto =
                bookingService.addBooking(createdBookingDto1, actualUserDto2.getId());

        BookingDto bookingDtoApproved =
                bookingService.changeStatus(bookingDto.getId(), true, actualUserDto1.getId());
        assertEquals(APPROVED, bookingDtoApproved.getStatus());
    }

    @Test
    @DirtiesContext
    void getById() {
        BookingDto bookingDto =
                bookingService.addBooking(createdBookingDto1, actualUserDto2.getId());

        assertEquals(bookingDto.getId(),
                bookingService.getById(bookingDto.getId(), actualUserDto1.getId()).getId());
    }

    @Test
    @DirtiesContext
    void getByUserIdAndStateByBooker() {
        BookingDto bookingDto =
                bookingService.addBooking(createdBookingDto1, actualUserDto2.getId());

        int from = 0;
        int size = 10;

        List<BookingDto> bookingsDto = bookingService.getByUserIdAndStateByBooker(
                actualUserDto2.getId(), "ALL", from, size);

        assertEquals(1, bookingsDto.size());
        assertEquals(bookingDto.getId(),
                bookingsDto.get(0).getId());
    }

    @Test
    @DirtiesContext
    void getByUserIdAndStateByOwner() {
        BookingDto bookingDto =
                bookingService.addBooking(createdBookingDto1, actualUserDto2.getId());

        int from = 0;
        int size = 10;

        List<BookingDto> bookingsDto = bookingService.getByUserIdAndStateByOwner(
                actualUserDto1.getId(), "ALL", from, size);

        assertEquals(1, bookingsDto.size());
        assertEquals(bookingDto.getId(),
                bookingsDto.get(0).getId());
    }
}