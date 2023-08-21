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
        log.info("+BookingController - addBooking: bookingDto = " + bookingDto + ", userId = " + userId);
        BookingDto answer =  bookingService.addBooking(bookingDto, userId);
        log.info("-BookingController - addBooking: answer = " + answer);
        return answer;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable long bookingId, @RequestParam Boolean approved,
                                   @RequestHeader(HEADER) long userId) {
        log.info("+BookingController - changeStatus: bookingId = " + bookingId + ", approved = "
                + approved + ", userId = " + userId);
        BookingDto answer = bookingService.changeStatus(bookingId, approved, userId);
        log.info("+BookingController - changeStatus: answer = " + answer);
        return answer;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId, @RequestHeader(HEADER) long userId) {
        log.info("+BookingController - getById: bookingId = " + bookingId +  ", userId = " + userId);
        BookingDto answer = bookingService.getById(bookingId, userId);
        log.info("+BookingController - getById: answer = " + answer);
        return answer;
    }

    @GetMapping
    public List<BookingDto> getByUserId(@RequestHeader(HEADER) long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        boolean isBooker = true;
        log.info("+BookingController - getByUserId: userId = " + userId +  ", state = "
                + state + ", isBooker = " + isBooker);
        List<BookingDto> answer =  bookingService.getByUserId(userId, state, isBooker);
        log.info("+BookingController - getByUserId: answer = " + answer);
        return answer;
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerId(@RequestHeader(HEADER) long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        boolean isBooker = false;
        log.info("+BookingController - getByOwnerId: userId = " + userId +  ", state = "
                + state + ", isBooker = " + isBooker);
        List<BookingDto> answer = bookingService.getByUserId(userId, state, isBooker);
        log.info("+BookingController - getByOwnerId: answer = " + answer);
        return answer;
    }
}
