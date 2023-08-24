package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Data
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final BookingMapper mapper = BookingMapper.INSTANCE;

    @Override
    public Booking addBooking(BookingDto bookingDto, long userId) {
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
        Booking answer = bookingRepository.save(mapper.bookingDtoToBooking(bookingDto));
        log.debug("-BookingServiceImpl - addBooking: " + answer);
        return answer;
    }

    @Override
    public Booking changeStatus(Long bookingId, boolean approved, long userId) {
        log.debug("+BookingServiceImpl - changeStatus: bookingId = " + bookingId + ", approved = " + approved + ", userId = " + userId);
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

        Booking answer = bookingRepository.save(booking);
        log.debug("-BookingServiceImpl - changeStatus: " + answer);
        return answer;
    }

    @Override
    public Booking getById(long bookingId, long userId) {
        log.debug("+BookingServiceImpl - getById: bookingId = " + bookingId + ", userId = " + userId);
        checkUser(userId);
        Booking booking = bookingRepository.findByIdOrderByIdDesc(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("bookingId = " + bookingId + " not found"));

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
    public List<Booking> getByUserIdAndStateByBooker(long userId, String state) {
        log.debug("+BookingServiceImpl - getByUserIdAndStateByBooker: userId = " + userId + ", state = " + state);
        boolean isBooker = true;
        List<Booking> answer = getByUserIdAndState(userId, state, isBooker);
        log.debug("-BookingServiceImpl - getByUserIdAndStateByBooker: " + answer);
        return answer;
    }

    @Override
    public List<Booking> getByUserIdAndStateByOwner(long userId, String state) {
        log.debug("+BookingServiceImpl - getByUserIdAndStateByOwner: userId = " + userId + ", state = " + state);
        boolean isBooker = false;
        List<Booking> answer = getByUserIdAndState(userId, state, isBooker);
        log.debug("+BookingServiceImpl - getByUserIdAndStateByOwner: userId = " + userId + ", state = " + state);
        return answer;
    }

    @Override
    public LastOrNextBooking getLastBooking(ItemDto item, long ownerId) {
        boolean isLastBooking = true;
        return getBookingToItem(item, ownerId, isLastBooking);
    }

    @Override
    public LastOrNextBooking getNextBooking(ItemDto item, long ownerId) {
        boolean isLastBooking = false;
        return getBookingToItem(item, ownerId, isLastBooking);
    }

    private LastOrNextBooking getBookingToItem(ItemDto item, long ownerId, boolean isLastBooking) {
        log.debug("+ItemServiceImpl - getBookingToItem: ItemDtoWithBooking = " + item + ", ownerId = " + ownerId);
        List<LastOrNextBooking> bookings = bookingRepository
                .findByItemIdOrderByStartAsc(item.getId())
                .stream()
                .filter(b -> b.getStatus().equals(APPROVED))
                .collect(Collectors.toList());

        log.debug("ItemServiceImpl - getBookingToItem: bookings = " + bookings + ", LocalDateTime.now() = " + LocalDateTime.now());
        LastOrNextBooking answer = null;
        if (item.getOwner().getId() == ownerId && !bookings.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            int lastBookingIndex = 0;
            for (int i = 0; i < bookings.size(); i++) {
                if (bookings.get(i).getStart().isBefore(now)) {
                    lastBookingIndex = i;
                }
            }
            int nextBookingIndex = lastBookingIndex + 1;
            if (isLastBooking) {
                answer =  bookings.get(lastBookingIndex);
            } else if (bookings.size() > nextBookingIndex) {
                answer =  bookings.get(nextBookingIndex);
            }
        }
        log.debug("-ItemServiceImpl - getBookingToItem: ItemDtoWithBooking = " + item);
        return answer;
    }

    private List<Booking> getByUserIdAndState(long userId, String state, boolean isBooker) {
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

        return booking.stream()
                .collect(toList());
    }

    private void checkUser(long userId) {
        if (Boolean.FALSE.equals(userRepository.existsById(userId))) {
            throw new ObjectNotFoundException("userId = " + userId + " not found");
        }
    }
}


