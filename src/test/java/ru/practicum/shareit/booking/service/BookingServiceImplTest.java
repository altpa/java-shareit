package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private static final BookingMapper mapper = BookingMapper.INSTANCE;
    private static final EasyRandom generator = new EasyRandom();

    private static final int PAGE = 0;
    private static final int FROM = 0;
    private static final int SIZE = 3;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private LocalDateTime now;
    private Page<Booking> page;
    private List<Booking> bookings;

    @BeforeEach
    public void setUp() {
        now = LocalDateTime.now();

        user1 = generator.nextObject(User.class);
        user1.setId(1);
        user2 = generator.nextObject(User.class);
        user2.setId(2);
        user3 = generator.nextObject(User.class);
        user3.setId(3);

        item1 = generator.nextObject(Item.class);
        item1.setOwner(user1);
        item1.setId(1);
        item2 = generator.nextObject(Item.class);
        item2.setOwner(user1);
        item2.setId(2);
        item3 = generator.nextObject(Item.class);
        item3.setOwner(user2);
        item3.setId(3);

        booking1 = generator.nextObject(Booking.class);
        booking1.setId(1);
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStart(now.plusDays(1));
        booking1.setEnd(now.plusDays(2));
        booking1.setStatus(WAITING);

        booking2 = generator.nextObject(Booking.class);
        booking2.setId(2);
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStart(now.plusDays(3));
        booking2.setEnd(now.plusDays(4));
        booking2.setStatus(WAITING);

        booking3 = generator.nextObject(Booking.class);
        booking3.setId(3);
        booking3.setBooker(user1);
        booking3.setItem(item3);
        booking3.setStart(now.plusDays(5));
        booking3.setEnd(now.plusDays(6));
        booking3.setStatus(WAITING);

        bookings = List.of(booking1, booking2, booking3);

        Pageable pageable = PageRequest.of(PAGE, SIZE);
        page = new PageImpl<>(bookings, pageable, SIZE);
    }

    @Test
    void addBooking() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking1);

        BookingDto bookingDto1 =
                bookingService.addBooking(mapper.bookingToBookingDto(booking1), user2.getId());

        verify(bookingRepository).save(Mockito.any());
        assertEquals(booking1.getId(), bookingDto1.getId());
    }

    @Test
    void addBookingWhenStartIsAfterEnd() {
        booking1.setStart(now.plusDays(3));

        verify(bookingRepository, never()).save(Mockito.any());

        assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(mapper.bookingToBookingDto(booking1), user2.getId());
        });
    }

    @Test
    void addBookingWhenStartIsEqualsEnd() {
        booking1.setStart(booking1.getEnd());

        verify(bookingRepository, never()).save(Mockito.any());

        assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(mapper.bookingToBookingDto(booking1), user2.getId());
        });
    }

    @Test
    void addBookingWhenItemNotAvailable() {
        item1.setAvailable(false);
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));

        verify(bookingRepository, never()).save(Mockito.any());

        assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(mapper.bookingToBookingDto(booking1), user2.getId());
        });
    }

    @Test
    void addBookingWhenOwnerIsUser() {
        item1.setAvailable(true);
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));

        verify(bookingRepository, never()).save(Mockito.any());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.addBooking(mapper.bookingToBookingDto(booking1), user1.getId());
        });
    }

    @Test
    void addBookingWhenBookerNotFound() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        verify(bookingRepository, never()).save(Mockito.any());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.addBooking(mapper.bookingToBookingDto(booking1), user2.getId());
        });
    }

    @Test
    void addBookingWhenItemNotFound() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        verify(bookingRepository, never()).save(Mockito.any());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.addBooking(mapper.bookingToBookingDto(booking1), user2.getId());
        });
    }

    @Test
    void changeStatus() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        booking1.setStatus(APPROVED);
        when(bookingRepository.save(Mockito.any())).thenReturn(booking1);

        booking1.setStatus(WAITING);
        BookingDto bookingDto =
                bookingService.changeStatus(booking1.getId(), true, user1.getId());

        verify(bookingRepository).save(Mockito.any());
        assertEquals(APPROVED, bookingDto.getStatus());
    }

    @Test
    void changeStatusWhenRejected() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        booking1.setStatus(REJECTED);
        when(bookingRepository.save(Mockito.any())).thenReturn(booking1);

        booking1.setStatus(WAITING);
        BookingDto bookingDto =
                bookingService.changeStatus(booking1.getId(), false, user1.getId());

        verify(bookingRepository).save(Mockito.any());
        assertEquals(REJECTED, bookingDto.getStatus());
    }

    @Test
    void changeStatusWhenAlreadyApproved() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        booking1.setStatus(APPROVED);

        verify(bookingRepository, never()).save(Mockito.any());
        assertThrows(BadRequestException.class, () -> {
            bookingService.changeStatus(booking1.getId(), true, user1.getId());
        });
    }

    @Test
    void changeStatusWhenOwnerIsNotUser() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));

        verify(bookingRepository, never()).save(Mockito.any());
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.changeStatus(booking1.getId(), true, user2.getId());
        });
    }

    @Test
    void changeStatusWhenBookingNotFound() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        verify(bookingRepository, never()).save(Mockito.any());
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.changeStatus(booking1.getId(), true, user2.getId());
        });
    }

    @Test
    void getByIdWhenBookerIdIsUserId() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        assertEquals(booking1.getId(), bookingService.getById(booking1.getId(), user2.getId()).getId());
    }

    @Test
    void getByIdWhenOwnerIdIsUserId() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        assertEquals(booking1.getId(), bookingService.getById(booking1.getId(), user1.getId()).getId());
    }

    @Test
    void getByIdWhenBookingNotFund() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong())).thenReturn(Optional.empty());
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.getById(booking1.getId(), user2.getId());
        });
    }

    @Test
    void getByIdWhenOwnerIdOrBookerIdIsNotUserId() {
        when(bookingRepository.findByIdOrderByIdDesc(Mockito.anyLong())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.getById(booking1.getId(), user3.getId());
        });
    }

    @Test
    void getByUserIdAndStateByBookerWhenStateAll() {
        String state = "ALL";

        when(bookingRepository
                .findByBookerIdOrderByIdDesc(Mockito.anyLong(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByBookerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByBooker(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByBookerWhenStateAllAndTotalLess() {
        String state = "ALL";

        when(bookingRepository
                .findByBookerIdOrderByIdDesc(Mockito.anyLong(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByBookerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByBooker(user1.getId(), state, FROM + 1, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenStateAllAndTotalLess() {
        String state = "ALL";

        when(bookingRepository
                .findByItemOwnerIdOrderByIdDesc(Mockito.anyLong(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByItemOwnerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM + 1, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenStateAll() {
        String state = "ALL";

        when(bookingRepository
                .findByItemOwnerIdOrderByIdDesc(Mockito.anyLong(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByItemOwnerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByBookerWhenStateCurrent() {
        String state = "CURRENT";

        when(bookingRepository
                .findByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(Mockito.anyLong(),
                        any(), any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByBookerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByBooker(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenStateCurrent() {
        String state = "CURRENT";

        when(bookingRepository
                .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByIdAsc(Mockito.anyLong(),
                        any(), any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByItemOwnerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByBookerWhenStatePast() {
        String state = "PAST";

        when(bookingRepository
                .findByBookerIdAndEndBeforeOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByBookerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByBooker(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenStatePast() {
        String state = "PAST";

        when(bookingRepository
                .findByItemOwnerIdAndEndBeforeOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByItemOwnerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByBookerWhenStateFuture() {
        String state = "FUTURE";

        when(bookingRepository
                .findByBookerIdAndStartAfterOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByBookerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByBooker(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenStateFuture() {
        String state = "FUTURE";

        when(bookingRepository
                .findByItemOwnerIdAndStartAfterOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByItemOwnerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByBookerWhenStateWaiting() {
        String state = "WAITING";

        when(bookingRepository
                .findByBookerIdAndStatusOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByBookerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByBooker(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenStateWaiting() {
        String state = "WAITING";

        when(bookingRepository
                .findByItemOwnerIdAndStatusOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByItemOwnerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByBookerWhenStateRejected() {
        String state = "REJECTED";

        when(bookingRepository
                .findByBookerIdAndStatusOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByBookerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByBooker(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenStateRejected() {
        String state = "REJECTED";

        when(bookingRepository
                .findByItemOwnerIdAndStatusOrderByIdDesc(Mockito.anyLong(),
                        any(), any()))
                .thenReturn(page);

        when(bookingRepository
                .countByItemOwnerId(Mockito.anyLong()))
                .thenReturn((long) SIZE);

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        List<BookingDto> answerDto =
                bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);

        List<Booking> answer = answerDto.stream().map(mapper::bookingDtoToBooking).collect(toList());

        assertEquals(bookings, answer);
    }

    @Test
    void getByUserIdAndStateByOwnerWhenUnknownState() {
        String state = "UNKNOWN";

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(true);

        assertThrows(ObjectsDbException.class, () -> {
            List<BookingDto> answerDto =
                    bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);
        });
    }

    @Test
    void getByUserIdAndStateByOwnerWhenNoUser() {
        String state = "ALL";

        when(userRepository
                .existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            List<BookingDto> answerDto =
                    bookingService.getByUserIdAndStateByOwner(user1.getId(), state, FROM, SIZE);
        });
    }
}