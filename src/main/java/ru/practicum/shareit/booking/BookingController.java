package ru.practicum.shareit.booking;

import lombok.Data;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Data
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;
    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(HEADER) long userId) {

        return bookingService.addBooking(bookingDto, userId);
    }

}
