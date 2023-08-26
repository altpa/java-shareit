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
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - addBooking: bookingDto = " + bookingDto + ", userId = " + userId);
        BookingDto answer =  bookingService.addBooking(bookingDto, userId);
        log.debug("-BookingController - addBooking: answer = " + answer);
        return answer;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable long bookingId, @RequestParam Boolean approved,
                                   @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - changeStatus: bookingId = " + bookingId + ", approved = "
                + approved + ", userId = " + userId);
        BookingDto answer = bookingService.changeStatus(bookingId, approved, userId);
        log.debug("+BookingController - changeStatus: answer = " + answer);
        return answer;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId, @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - getById: bookingId = " + bookingId +  ", userId = " + userId);
        BookingDto answer = bookingService.getById(bookingId, userId);
        log.debug("+BookingController - getById: answer = " + answer);
        return answer;
    }

    @GetMapping
    public List<BookingDto> getByUserIdAndStateByBooker(@RequestHeader(HEADER) long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        log.debug("+BookingController - getByUserIdAndState: userId = " + userId +  ", state = " + state);
        List<BookingDto> answer =  bookingService.getByUserIdAndStateByBooker(userId, state);

        log.debug("+BookingController - getByUserIdAndState: answer = " + answer);
        return answer;
    }

    @GetMapping("/owner")
    public List<BookingDto> getByUserIdAndStateByOwner(@RequestHeader(HEADER) long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        log.debug("+BookingController - getByOwnerId: userId = " + userId +  ", state = " + state);
        List<BookingDto> answer = bookingService.getByUserIdAndStateByOwner(userId, state);
        log.debug("+BookingController - getByOwnerId: answer = " + answer);
        return answer;
    }
}
