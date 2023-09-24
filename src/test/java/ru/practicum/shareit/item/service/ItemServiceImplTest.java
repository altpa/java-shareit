package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private static final int PAGE = 0;
    private static final int SIZE = 10;

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

    private User createdUser1;
    private User createdUser2;
    private Item createdItem1;
    private Comment createdComment1;
    private Booking createdBooking1;
    private LastOrNextBooking createdLastOrNextBooking1;
    private LastOrNextBooking createdLastOrNextBooking2;
    private Page<Item> page;
    private Page<Item> emptyPage;

    @BeforeEach
    public void setUp() {
        createdUser1 = generator.nextObject(User.class);
        createdUser2 = generator.nextObject(User.class);

        createdItem1 = generator.nextObject(Item.class);
        createdItem1.setOwner(createdUser1);

        createdBooking1 = generator.nextObject(Booking.class);

        createdLastOrNextBooking1 = generator.nextObject(LastOrNextBooking.class);
        createdLastOrNextBooking2 = generator.nextObject(LastOrNextBooking.class);

        createdComment1 = generator.nextObject(Comment.class);

        Pageable pageable = PageRequest.of(PAGE, SIZE);
        page = new PageImpl<>(List.of(createdItem1), pageable, SIZE);
        emptyPage = new PageImpl<>(Collections.emptyList(), pageable, SIZE);

    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createdUser1));
        when(itemRepository.save(any())).thenReturn(createdItem1);

        ItemDto itemDto1 =
                itemService.addItem(itemMapper.itemToItemDto(createdItem1), createdUser1.getId(), true);

        assertEquals(itemDto1.getId(), createdItem1.getId());
    }

    @Test
    void addItemWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            ItemDto itemDto1 =
                    itemService.addItem(itemMapper.itemToItemDto(createdItem1), createdUser1.getId(), true);
        });
    }

    @Test
    void getAllItemsByOwnerIdWhenNoComments() {
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(commentRepository.findByItemId(anyLong()))
                .thenThrow(new InvalidDataAccessResourceUsageException("no comments"));

        assertThrows(ObjectNotFoundException.class, () -> {
            List<ItemDto> itemsDto =
                    itemService.getAllItemsByOwnerId(createdUser1.getId());
        });
    }

    @Test
    void getAllItemsByOwnerIdWhenLastAndNextBooking() {
        createdItem1.setOwner(createdUser1);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(createdComment1)));

        createdLastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        createdLastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        createdLastOrNextBooking1.setStatus(APPROVED);
        createdLastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        createdLastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        createdLastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(createdLastOrNextBooking1, createdLastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(createdUser1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), createdItem1.getId());
        assertEquals(itemsDto.get(0).getLastBooking().getId(), createdLastOrNextBooking1.getId());
        assertEquals(itemsDto.get(0).getNextBooking().getId(), createdLastOrNextBooking2.getId());
    }

    @Test
    void getAllItemsByOwnerIdWhenOwnerIsNotOwner() {
        createdItem1.setOwner(createdUser1);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(createdComment1)));

        createdLastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        createdLastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        createdLastOrNextBooking1.setStatus(APPROVED);
        createdLastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        createdLastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        createdLastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(createdLastOrNextBooking1, createdLastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(createdUser2.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), createdItem1.getId());
        assertNull(itemsDto.get(0).getNextBooking());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenNotApproved() {
        createdItem1.setOwner(createdUser1);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(createdComment1)));

        createdLastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        createdLastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        createdLastOrNextBooking1.setStatus(WAITING);
        createdLastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        createdLastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        createdLastOrNextBooking2.setStatus(WAITING);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(createdLastOrNextBooking1, createdLastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(createdUser1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), createdItem1.getId());
        assertNull(itemsDto.get(0).getNextBooking());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenNoBooking() {
        createdItem1.setOwner(createdUser1);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(createdComment1)));

        createdLastOrNextBooking1.setStart(LocalDateTime.now().minusDays(2));
        createdLastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(1));
        createdLastOrNextBooking1.setStatus(WAITING);
        createdLastOrNextBooking2.setStart(LocalDateTime.now().plusDays(1));
        createdLastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(2));
        createdLastOrNextBooking2.setStatus(WAITING);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(Collections.emptyList()));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(createdUser1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), createdItem1.getId());
        assertNull(itemsDto.get(0).getNextBooking());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenOnlyLastBooking() {
        createdItem1.setOwner(createdUser1);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(createdComment1)));

        createdLastOrNextBooking1.setStart(LocalDateTime.now().minusDays(4));
        createdLastOrNextBooking1.setEnd(LocalDateTime.now().minusDays(3));
        createdLastOrNextBooking1.setStatus(APPROVED);
        createdLastOrNextBooking2.setStart(LocalDateTime.now().minusDays(2));
        createdLastOrNextBooking2.setEnd(LocalDateTime.now().minusDays(1));
        createdLastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(createdLastOrNextBooking1, createdLastOrNextBooking2)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(createdUser1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getId(), createdItem1.getId());
        assertEquals(itemsDto.get(0).getLastBooking().getId(), createdLastOrNextBooking2.getId());
        assertNull(itemsDto.get(0).getNextBooking());
    }

    @Test
    void getAllItemsByOwnerIdWhenOnlyNextBooking() {
        createdItem1.setOwner(createdUser1);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(List.of(createdComment1)));

        createdLastOrNextBooking1.setStart(LocalDateTime.now().plusDays(1));
        createdLastOrNextBooking1.setEnd(LocalDateTime.now().plusDays(2));
        createdLastOrNextBooking1.setStatus(APPROVED);
        createdLastOrNextBooking2.setStart(LocalDateTime.now().plusDays(3));
        createdLastOrNextBooking2.setEnd(LocalDateTime.now().plusDays(4));
        createdLastOrNextBooking2.setStatus(APPROVED);

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(List.of(createdLastOrNextBooking2, createdLastOrNextBooking1)));

        List<ItemDto> itemsDto = itemService.getAllItemsByOwnerId(createdUser1.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getNextBooking().getId(), createdLastOrNextBooking1.getId());
        assertNull(itemsDto.get(0).getLastBooking());
    }

    @Test
    void addComment() {
        CommentDto commentDto = commentMapper.commentToCommentDto(createdComment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(createdBooking1)));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createdItem1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createdUser1));

        when(commentRepository.save(any())).thenReturn(createdComment1);

        CommentDto answer = itemService.addComment(commentDto, createdUser1.getId(), createdItem1.getId());

        assertEquals(answer.getText(), commentDto.getText());
    }

    @Test
    void addCommentWhenNotBooked() {
        CommentDto commentDto = commentMapper.commentToCommentDto(createdComment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(Collections.emptyList()));

        assertThrows(BadRequestException.class, () -> {
            CommentDto answer = itemService.addComment(commentDto, createdUser1.getId(), createdItem1.getId());
        });
    }

    @Test
    void addCommentWhenItemNotFound() {
        CommentDto commentDto = commentMapper.commentToCommentDto(createdComment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(createdBooking1)));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            CommentDto answer = itemService.addComment(commentDto, createdUser1.getId(), createdItem1.getId());
        });
    }

    @Test
    void addCommentWhenUserNotFound() {
        CommentDto commentDto = commentMapper.commentToCommentDto(createdComment1);

        when(bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(createdBooking1)));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createdItem1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            CommentDto answer = itemService.addComment(commentDto, createdUser1.getId(), createdItem1.getId());
        });
    }

    @Test
    void updateItem() {
        createdItem1.setOwner(createdUser1);
        ItemDto itemDto = itemMapper.itemToItemDto(createdItem1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createdItem1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createdUser1));

        when(itemRepository.save(any())).thenReturn(createdItem1);

        ItemDto answer = itemService.updateItem(itemDto, createdUser1.getId(), createdItem1.getId());

        assertEquals(createdItem1.getId(), answer.getId());
    }

    @Test
    void updateItemWhenNoItem() {
        createdItem1.setOwner(createdUser1);
        ItemDto itemDto = itemMapper.itemToItemDto(createdItem1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectsDbException.class, () -> {
            ItemDto answer = itemService.updateItem(itemDto, createdUser1.getId(), createdItem1.getId());
        });
    }

    @Test
    void updateItemWhenNotOwner() {
        createdItem1.setOwner(createdUser2);
        ItemDto itemDto = itemMapper.itemToItemDto(createdItem1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createdItem1));

        assertThrows(ObjectsDbException.class, () -> {
            ItemDto answer = itemService.updateItem(itemDto, createdUser1.getId(), createdItem1.getId());
        });
    }

    @Test
    void updateItemWhenOwnerNotFound() {
        createdItem1.setOwner(createdUser1);
        ItemDto itemDto = itemMapper.itemToItemDto(createdItem1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createdItem1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            ItemDto answer = itemService.updateItem(itemDto, createdUser1.getId(), createdItem1.getId());
        });
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createdItem1));

        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(Streamable.of(Collections.emptyList()));

        when(bookingRepository.findByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(Streamable.of(Collections.emptyList()));

        ItemDto answer = itemService.getItemById(createdItem1.getId(), createdItem1.getOwner().getId());

        assertEquals(answer.getId(), createdItem1.getId());
    }

    @Test
    void getItemByIdWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            ItemDto answer = itemService.getItemById(createdItem1.getId(), createdItem1.getOwner().getId());
        });
    }

    @Test
    void searchItems() {
        createdItem1.setName("Name");

        when(itemRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        any(String.class), any(String.class), any(Pageable.class)))
                .thenReturn(page);

        List<ItemDto> itemsDto = itemService.searchItems("Name");

        assertEquals(1, itemsDto.size());
        assertEquals(itemsDto.get(0).getName(), createdItem1.getName());
    }

    @Test
    void searchItemsWhenNotFound() {
        when(itemRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        any(String.class), any(String.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        List<ItemDto> itemsDto = itemService.searchItems("Name");
        System.out.println(itemsDto);
        assertEquals(0, itemsDto.size());
    }

    @Test
    void searchItemsWhenEmptySearch() {
        List<ItemDto> itemsDto = itemService.searchItems("");

        assertEquals(0, itemsDto.size());
    }
}