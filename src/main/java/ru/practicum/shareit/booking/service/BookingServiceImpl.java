package ru.practicum.shareit.booking.service;

import com.sun.source.tree.LambdaExpressionTree;
import lombok.Data;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Data
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final BookingMapper mapper = BookingMapper.INSTANCE;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("itemId = " + bookingDto.getItemId() + " not found"));

        if (!item.getAvailable()) {
            throw new BadRequestException("item: " + bookingDto.getItemId() + " is not available");
        }

        bookingDto.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("userId = " + userId + " not found")));

        return mapper.bookingToBookingDto(bookingRepository.save(mapper.bookingDtoToBooking(bookingDto)));
    }

    @Override
    public List<BookingDto> getAllBookings(long bookingId) {
        return null;
    }

    @Override
    public BookingDto getBookingById(long bookingId) {
        return null;
    }
}
