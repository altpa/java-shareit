package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
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

@Data
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private static final BookingStatus APPROVED = BookingStatus.APPROVED;
    private static final BookingStatus REJECTED = BookingStatus.REJECTED;
    private static final BookingStatus WAITING = BookingStatus.WAITING;
    private static final BookingStatus CURRENT = BookingStatus.CURRENT;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final BookingMapper mapper = BookingMapper.INSTANCE;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, long userId) {
        log.info("+BookingServiceImpl - addBooking: " + bookingDto + ", userId = " + userId);
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
        log.info("-BookingServiceImpl - addBooking: " + answer);
        return answer;
    }

    @Override
    public BookingDto changeStatus(Long bookingId, boolean approved, long userId) {
        log.info("+BookingServiceImpl - changeStatus: bookingId = " + bookingId + ", approved = " + approved + ", userId = " + userId);
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
        log.info("-BookingServiceImpl - changeStatus: " + answer);
        return answer;
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        log.info("+BookingServiceImpl - getById: bookingId = " + bookingId + ", userId = " + userId);
        checkUser(userId);
        Booking booking = bookingRepository.findByIdOrderByIdDesc(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("bookingId = " + bookingId + " not found"));

        Item item = booking.getItem();
        long ownerId = item.getOwner().getId();
        long bookerId = booking.getBooker().getId();

        if (ownerId == userId || bookerId == userId) {
            BookingDto answer = mapper.bookingToBookingDto(booking);
            log.info("-BookingServiceImpl - getById: " + answer);
            return answer;
        } else {
            throw new ObjectNotFoundException("userId = " + userId + ", ownerId = " + ownerId + ", bookerId = "
                    + bookerId);
        }
    }

    @Override
    public List<BookingDto> getByUserId(long userId, String state, boolean isBooker) {
        log.info("+BookingServiceImpl - getByUserId: userId = " + userId + ", state = " + state + ", isBooker = "
                + isBooker);

        Streamable<Booking> booking;
        checkUser(userId);
        switch (state) {
            case "ALL":
                if (isBooker) {
                    booking = bookingRepository.findByBookerIdOrderByIdDesc(userId);
                } else {
                    booking = bookingRepository.findByItemOwnerIdOrderByIdDesc(userId);
                }
                break;
            case "CURRENT":
                LocalDateTime now = LocalDateTime.now();
                if (isBooker) {
                    booking = bookingRepository
                            .findByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(userId, now, now);
                } else {
                    booking = bookingRepository
                            .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByIdAsc(userId, now, now);
                }
                break;
            case "PAST":
                if (isBooker) {
                    booking = bookingRepository.findByBookerIdAndEndBeforeOrderByIdDesc(userId, LocalDateTime.now());
                } else {
                    booking = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByIdDesc(userId, LocalDateTime.now());
                }
                break;
            case "FUTURE":
                if (isBooker) {
                    booking = bookingRepository.findByBookerIdAndStartAfterOrderByIdDesc(userId, LocalDateTime.now());
                } else {
                    booking = bookingRepository.findByItemOwnerIdAndStartAfterOrderByIdDesc(userId, LocalDateTime.now());
                }
                break;
            case "WAITING":
                if (isBooker) {
                    booking = bookingRepository.findByBookerIdAndStatusOrderByIdDesc(userId, WAITING);
                } else {
                    booking = bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(userId, WAITING);
                }
                break;
            case "REJECTED":
                if (isBooker) {
                    booking = bookingRepository.findByBookerIdAndStatusOrderByIdDesc(userId, REJECTED);
                } else {
                    booking = bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(userId, REJECTED);
                }
                break;
            default:
                throw new ObjectsDbException("Unknown state: " + state);
        }

        List<BookingDto> answer = booking.stream()
                .map(mapper::bookingToBookingDto)
                .collect(toList());
        log.info("-BookingServiceImpl - getByUserId: " + answer);
        return answer;
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("userId = " + userId + " not found");
        }
    }
}


