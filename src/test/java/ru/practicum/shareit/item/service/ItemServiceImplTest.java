package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.util.Streamable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private static final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private static final CommentMapper commentMapper = CommentMapper.INSTANCE;

    private static final EasyRandom generator = new EasyRandom();

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Booking booking1;
    private LastOrNextBooking lastOrNextBooking1;
    private LastOrNextBooking lastOrNextBooking2;

    @BeforeEach
    public void setUp() {
        user1 = generator.nextObject(User.class);
        user2 = generator.nextObject(User.class);

        item1 = generator.nextObject(Item.class);
        item2 = generator.nextObject(Item.class);
        item1.setOwner(user1);

        booking1 = generator.nextObject(Booking.class);

        lastOrNextBooking1 = generator.nextObject(LastOrNextBooking.class);
        lastOrNextBooking2 = generator.nextObject(LastOrNextBooking.class);

        comment1 = generator.nextObject(Comment.class);
    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto itemDto1 =
                itemService.addItem(itemMapper.itemToItemDto(item1), user1.getId(), true);

        assertEquals(itemDto1.getId(), item1.getId());
    }

    @Test
    void addItemWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            ItemDto itemDto1 =
                    itemService.addItem(itemMapper.itemToItemDto(item1), user1.getId(), true);
        });
    }

    @Test
    void getAllItemsByOwnerIdWhenNoComments() {
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(item1)));
        when(commentRepository.findByItemId(anyLong()))
                .thenThrow(new InvalidDataAccessResourceUsageException("no comments"));

        assertThrows(ObjectNotFoundException.class, () -> {
            List<ItemDto> itemsDto =
                    itemService.getAllItemsByOwnerId(user1.getId());
        });
    }

    @Test
    void getAllItemsByOwnerIdWhenLastAndNextBooking() {
        item1.setOwner(user1);
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(item1)));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(comment1)));

        lastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        lastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        lastOrNextBooking1.setStatus(APPROVED);
        lastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        lastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        lastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(lastOrNextBooking1, lastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(user1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), item1.getId());
        assertEquals(itemsDto.get(0).getLastBooking().getId(), lastOrNextBooking1.getId());
        assertEquals(itemsDto.get(0).getNextBooking().getId(), lastOrNextBooking2.getId());
    }

    @Test
    void getAllItemsByOwnerIdWhenOwnerIsNotOwner() {
        item1.setOwner(user1);
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(item1)));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(comment1)));

        lastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        lastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        lastOrNextBooking1.setStatus(APPROVED);
        lastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        lastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        lastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(lastOrNextBooking1, lastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(user2.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), item1.getId());
        assertNull(itemsDto.get(0).getNextBooking());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenNotApproved() {
        item1.setOwner(user1);
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(item1)));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(comment1)));

        lastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        lastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        lastOrNextBooking1.setStatus(WAITING);
        lastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        lastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        lastOrNextBooking2.setStatus(WAITING);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(lastOrNextBooking1, lastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(user1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), item1.getId());
        assertNull(itemsDto.get(0).getNextBooking());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenNoBooking() {
        item1.setOwner(user1);
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(item1)));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(comment1)));

        lastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        lastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        lastOrNextBooking1.setStatus(WAITING);
        lastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        lastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        lastOrNextBooking2.setStatus(WAITING);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(Collections.emptyList()));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(user1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), item1.getId());
        assertNull(itemsDto.get(0).getNextBooking());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenOnlyLastBooking() {
        item1.setOwner(user1);
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(item1)));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(comment1)));

        lastOrNextBooking1.setStart(LocalDateTime.now().minusDays(4));
        lastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(3));
        lastOrNextBooking1.setStatus(APPROVED);
        lastOrNextBooking2.setStart(LocalDateTime.now().minusDays(2));
        lastOrNextBooking2.setEnd(LocalDateTime.now().minusDays(1));
        lastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(lastOrNextBooking1, lastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(user1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), item1.getId());
        assertEquals(itemsDto.get(0).getLastBooking().getId(), lastOrNextBooking2.getId());
        assertNull(itemsDto.get(0).getNextBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenOnlyNextBooking() {
        item1.setOwner(user1);
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(item1)));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(comment1)));

        lastOrNextBooking1.setStart(LocalDateTime.now().plusDays(1));
        lastOrNextBooking1.setEnd(LocalDateTime.now().plusDays(2));
        lastOrNextBooking1.setStatus(APPROVED);
        lastOrNextBooking2.setStart(LocalDateTime.now().plusDays(3));
        lastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(4));
        lastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(lastOrNextBooking2, lastOrNextBooking1)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(user1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getNextBooking().getId(), lastOrNextBooking1.getId());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void addComment() {
        CommentDto commentDto = commentMapper.commentToCommentDto(comment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(booking1)));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        when(commentRepository.save(any())).thenReturn(comment1);

        CommentDto answer = itemService.addComment(commentDto, user1.getId(), item1.getId());

        assertEquals(answer.getText(), commentDto.getText());
    }

    @Test
    void addCommentWhenNotBooked() {
        CommentDto commentDto = commentMapper.commentToCommentDto(comment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(Collections.emptyList()));

        assertThrows(BadRequestException.class, () -> {
            CommentDto answer = itemService.addComment(commentDto, user1.getId(), item1.getId());
        });
    }

    @Test
    void addCommentWhenItemNotFound() {
        CommentDto commentDto = commentMapper.commentToCommentDto(comment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(booking1)));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            CommentDto answer = itemService.addComment(commentDto, user1.getId(), item1.getId());
        });
    }

    @Test
    void addCommentWhenUserNotFound() {
        CommentDto commentDto = commentMapper.commentToCommentDto(comment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(booking1)));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            CommentDto answer = itemService.addComment(commentDto, user1.getId(), item1.getId());
        });
    }

    @Test
    void updateItem() {
        item1.setOwner(user1);
        ItemDto itemDto = itemMapper.itemToItemDto(item1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto answer = itemService.updateItem(itemDto, user1.getId(), item1.getId());

        assertEquals(item1.getId(), answer.getId());
    }

    @Test
    void updateItemWhenNoItem() {
        item1.setOwner(user1);
        ItemDto itemDto = itemMapper.itemToItemDto(item1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectsDbException.class, () -> {
            ItemDto answer = itemService.updateItem(itemDto, user1.getId(), item1.getId());
        });
    }

    @Test
    void updateItemWhenNotOwner() {
        item1.setOwner(user2);
        ItemDto itemDto = itemMapper.itemToItemDto(item1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(ObjectsDbException.class, () -> {
            ItemDto answer = itemService.updateItem(itemDto, user1.getId(), item1.getId());
        });
    }

    @Test
    void updateItemWhenOwnerNotFound() {
        item1.setOwner(user1);
        ItemDto itemDto = itemMapper.itemToItemDto(item1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            ItemDto answer = itemService.updateItem(itemDto, user1.getId(), item1.getId());
        });
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(Collections.emptyList()));

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(Collections.emptyList()));

        ItemDto answer = itemService.getItemById(item1.getId(), item1.getOwner().getId());

        assertEquals(answer.getId(), item1.getId());
    }

    @Test
    void getItemByIdWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            ItemDto answer = itemService.getItemById(item1.getId(), item1.getOwner().getId());
        });
    }

    @Test
    void searchItems() {
        item1.setName("Name");

        when(itemRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        any(String.class), any(String.class)))
                .thenReturn(Streamable.of(List.of(item1)));

        List<ItemDto> itemsDto = itemService.searchItems("Name");

        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getName(), item1.getName());
    }

    @Test
    void searchItemsWhenNotFound() {
        when(itemRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        any(String.class), any(String.class)))
                .thenReturn(Streamable.empty());

        List<ItemDto> itemsDto = itemService.searchItems("Name");

        assertEquals(0, itemsDto.size());
    }

    @Test
    void searchItemsWhenEmptySearch() {
        List<ItemDto> itemsDto = itemService.searchItems("");

        assertEquals(0, itemsDto.size());
    }
}