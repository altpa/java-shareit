package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Data
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private static final BookingMapper mapper = BookingMapper.INSTANCE;

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, long userId) {
        log.debug("+BookingServiceImpl - addBooking: " + bookingDto + ", userId = " + userId);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BadRequestException("start = " + bookingDto.getStart() + " after or equals end = "
                    + bookingDto.getEnd());
        }

        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("itemId = " + itemId + " not found"));

        if (!item.getAvailable()) {
            throw new BadRequestException("item: " + item + " is not available");
        }

        if (item.getOwner().getId() == userId) {
            throw new ObjectNotFoundException("ownerId = " + item.getOwner().getId() + " is equals to userId");
        }

        bookingDto.setItem(item);

        bookingDto.setBooker(userRepository.findById(userId)
            .orElseThrow(() -> new ObjectNotFoundException("userId = " + userId + " not found")));

        bookingDto.setStatus(WAITING);
        BookingDto answer = mapper.bookingToBookingDto(bookingRepository.save(mapper.bookingDtoToBooking(bookingDto)));
        log.debug("-BookingServiceImpl - addBooking: " + answer);
        return answer;
    }

    @Override
    public BookingDto changeStatus(Long bookingId, boolean approved, long userId) {
        log.debug("+BookingServiceImpl - changeStatus: bookingId = " + bookingId +
                ", approved = " + approved + ", userId = " + userId);
        Booking booking = bookingRepository.findByIdOrderByIdDesc(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("bookingId = " + bookingId + " not found"));

        long ownerId = booking.getItem().getOwner().getId();

        if (ownerId == userId) {
            if (approved) {
                if (booking.getStatus().equals(APPROVED)) {
                    throw new BadRequestException("booking: " + booking + " already APPROVED");
                }
                booking.setStatus(APPROVED);
            } else {
                booking.setStatus(REJECTED);
            }
        } else {
            throw new ObjectNotFoundException("booking: " + booking + ", ownerId = " + ownerId
                    + " not equals userId = " + userId);
        }

        BookingDto answer = mapper.bookingToBookingDto(bookingRepository.save(booking));
        log.debug("-BookingServiceImpl - changeStatus: " + answer);
        return answer;
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        log.debug("+BookingServiceImpl - getById: bookingId = " + bookingId + ", userId = " + userId);
        checkUser(userId);
        BookingDto booking =
                mapper.bookingToBookingDto(bookingRepository.findByIdOrderByIdDesc(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("bookingId = " +
                        bookingId + " not found")));

        Item item = booking.getItem();
        long ownerId = item.getOwner().getId();
        long bookerId = booking.getBooker().getId();

        if (ownerId == userId || bookerId == userId) {
            log.debug("-BookingServiceImpl - getById: " + booking);
            return booking;
        } else {
            throw new ObjectNotFoundException("userId = " + userId + ", ownerId = " + ownerId + ", bookerId = "
                    + bookerId);
        }
    }

    @Override
    public List<BookingDto> getByUserIdAndStateByBooker(long userId, String state, int from, int size) {
        log.info("+BookingServiceImpl - getByUserIdAndStateByBooker: userId = " + userId + ", state = " + state
                + ", from = " + from + ", size = " + size);
        boolean isBooker = true;
        List<BookingDto> answer = getByUserIdAndState(userId, state, isBooker, from, size);
        log.info("-BookingServiceImpl - getByUserIdAndStateByBooker: " + answer);
        return answer;
    }

    @Override
    public List<BookingDto> getByUserIdAndStateByOwner(long userId, String state, int from, int size) {
        log.debug("+BookingServiceImpl - getByUserIdAndStateByOwner: userId = " + userId + ", state = " + state
                + ", from = " + from + ", size = " + size);
        boolean isBooker = false;
        List<BookingDto> answer = getByUserIdAndState(userId, state, isBooker, from, size);
        log.debug("+BookingServiceImpl - getByUserIdAndStateByOwner: userId = " + userId + ", state = " + state);
        return answer;
    }


    private List<BookingDto> getByUserIdAndState(long userId, String state, boolean isBooker, int from, int size) {
        Streamable<Booking> bookings;
        checkUser(userId);
        switch (state) {
            case "ALL":
                if (isBooker) {
                    bookings = bookingRepository
                            .findByBookerIdOrderByIdDesc(userId, getPage(isBooker, userId, from, size));
                } else {
                    bookings = bookingRepository
                            .findByItemOwnerIdOrderByIdDesc(userId, getPage(isBooker, userId, from, size));
                }
                break;
            case "CURRENT":
                LocalDateTime now = LocalDateTime.now();
                if (isBooker) {
                    bookings = bookingRepository
                            .findByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(
                                    userId, now, now, getPage(isBooker, userId, from, size));
                } else {
                    bookings = bookingRepository
                            .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByIdAsc(
                                    userId, now, now, getPage(isBooker, userId, from, size));
                }
                break;
            case "PAST":
                if (isBooker) {
                    bookings = bookingRepository
                            .findByBookerIdAndEndBeforeOrderByIdDesc(
                                    userId, LocalDateTime.now(), getPage(isBooker, userId, from, size));
                } else {
                    bookings = bookingRepository
                            .findByItemOwnerIdAndEndBeforeOrderByIdDesc(
                                    userId, LocalDateTime.now(), getPage(isBooker, userId, from, size));
                }
                break;
            case "FUTURE":
                if (isBooker) {
                    bookings = bookingRepository
                            .findByBookerIdAndStartAfterOrderByIdDesc(
                                    userId, LocalDateTime.now(), getPage(isBooker, userId, from, size));
                } else {
                    bookings = bookingRepository
                            .findByItemOwnerIdAndStartAfterOrderByIdDesc(
                                    userId, LocalDateTime.now(), getPage(isBooker, userId, from, size));
                }
                break;
            case "WAITING":
                if (isBooker) {
                    bookings = bookingRepository.findByBookerIdAndStatusOrderByIdDesc(
                            userId, WAITING, getPage(isBooker, userId, from, size));
                } else {
                    bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(
                            userId, WAITING, getPage(isBooker, userId, from, size));
                }
                break;
            case "REJECTED":
                if (isBooker) {
                    bookings = bookingRepository
                            .findByBookerIdAndStatusOrderByIdDesc(
                                    userId, REJECTED, getPage(isBooker, userId, from, size));
                } else {
                    bookings = bookingRepository
                            .findByItemOwnerIdAndStatusOrderByIdDesc(
                                    userId, REJECTED, getPage(isBooker, userId, from, size));
                }
                break;
            default:
                throw new ObjectsDbException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(mapper::bookingToBookingDto)
                .collect(toList());
    }

    private void checkUser(long userId) {
        if (Boolean.FALSE.equals(userRepository.existsById(userId))) {
            throw new ObjectNotFoundException("userId = " + userId + " not found");
        }
    }

    private Pageable getPage(boolean isBooker, long userId, int from, int size) {
        long total;
        if (isBooker) {
            total = bookingRepository.countByBookerId(userId);
        } else {
            total = bookingRepository.countByItemOwnerId(userId);
        }
        if (total < from + size) {
            size = (int) total - from;
        }
        return PageRequest.of(from, size);
    }
}


