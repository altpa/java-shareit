package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemServiceImplIntegrationTest {
    private static final EasyRandom generator = new EasyRandom();
    private static final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private static final BookingMapper bookingMapper = BookingMapper.INSTANCE;

    private static final CommentMapper commentMapper = CommentMapper.INSTANCE;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    ItemDto actualItemDto1;
    ItemDto actualItemDto;
    UserDto actualUserDto1;
    UserDto actualUserDto2;

    @BeforeEach
    public void setUp() {
        actualUserDto1 = userService.addUser(userMapper.userToUserDto(generator.nextObject(User.class)));
        actualUserDto2 = userService.addUser(userMapper.userToUserDto(generator.nextObject(User.class)));

        actualItemDto1 = itemMapper.itemToItemDto(generator.nextObject(Item.class));
        actualItemDto1.setOwner(userMapper.userDtoToUser(actualUserDto1));

        actualItemDto =
            itemService.addItem(actualItemDto1, actualUserDto1.getId(), userService.checkOwner(actualUserDto1.getId()));
    }

    @Test
    @DirtiesContext
    void addItem() {
        assertEquals(1, actualItemDto.getId());
    }

    @Test
    @DirtiesContext
    void getAllItemsByOwnerId() {
        assertEquals(1, itemService.getAllItemsByOwnerId(actualUserDto1.getId()).size());
        assertEquals(1, itemService.getAllItemsByOwnerId(actualUserDto1.getId()).get(0).getId());
    }

    @Test
    @DirtiesContext
    void updateItem() {
        ItemDto itemDto2 = itemMapper.itemToItemDto(generator.nextObject(Item.class));
        itemDto2.setName("New Name");

        assertEquals("New Name", itemService.updateItem(itemDto2, actualUserDto1.getId(), actualItemDto.getId()).getName());
    }

    @Test
    @DirtiesContext
    void getItemById() {
        assertEquals(1, itemService.getItemById(actualItemDto.getId(), actualUserDto1.getId()).getId());
    }

    @Test
    @DirtiesContext
    void searchItems() {
        assertEquals(1, itemService.searchItems(actualItemDto.getName()).size());
        assertEquals(1, itemService.searchItems(actualItemDto.getName()).get(0).getId());
    }

    @Test
    @DirtiesContext
    void addComment() {
        CommentDto commentDto = generator.nextObject(CommentDto.class);
        commentDto.setItem(itemMapper.itemDtoToItem(actualItemDto));

        Booking booking = generator.nextObject(Booking.class);
        BookingDto bookingDto = bookingMapper.bookingToBookingDto(booking);
        bookingDto.setItemId(actualItemDto.getId());
        bookingDto.setBookerId(actualUserDto2.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto actualBookingDto = bookingService.addBooking(bookingDto, actualUserDto2.getId());
        bookingService.changeStatus(actualBookingDto.getId(), true, actualUserDto1.getId());

        assertEquals(commentDto.getText(),
                itemService.addComment(commentDto, actualUserDto2.getId(), actualItemDto.getId()).getText());
    }
}