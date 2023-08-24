package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;
    private static final BookingMapper mapper = BookingMapper.INSTANCE;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - addBooking: bookingDto = " + bookingDto + ", userId = " + userId);
        Booking answer =  bookingService.addBooking(bookingDto, userId);
        log.debug("-BookingController - addBooking: answer = " + answer);
        return mapper.bookingToBookingDto(answer);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable long bookingId, @RequestParam Boolean approved,
                                   @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - changeStatus: bookingId = " + bookingId + ", approved = "
                + approved + ", userId = " + userId);
        Booking answer = bookingService.changeStatus(bookingId, approved, userId);
        log.debug("+BookingController - changeStatus: answer = " + answer);
        return mapper.bookingToBookingDto(answer);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId, @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - getById: bookingId = " + bookingId +  ", userId = " + userId);
        Booking answer = bookingService.getById(bookingId, userId);
        log.debug("+BookingController - getById: answer = " + answer);
        return mapper.bookingToBookingDto(answer);
    }

    @GetMapping
    public List<BookingDto> getByUserIdAndStateByBooker(@RequestHeader(HEADER) long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        log.debug("+BookingController - getByUserIdAndState: userId = " + userId +  ", state = " + state);
        List<Booking> answer =  bookingService.getByUserIdAndStateByBooker(userId, state);

        log.debug("+BookingController - getByUserIdAndState: answer = " + answer);
        return answer.stream().map(mapper::bookingToBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getByUserIdAndStateByOwner(@RequestHeader(HEADER) long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        log.debug("+BookingController - getByOwnerId: userId = " + userId +  ", state = " + state);
        List<Booking> answer = bookingService.getByUserIdAndStateByOwner(userId, state);
        log.debug("+BookingController - getByOwnerId: answer = " + answer);
        return answer.stream().map(mapper::bookingToBookingDto).collect(Collectors.toList());
    }
}
