package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Data
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - addBooking: bookingDto = " + bookingDto + ", userId = " + userId);
        ResponseEntity<Object>  answer =  bookingClient.addBooking(bookingDto, userId);
        log.debug("-BookingController - addBooking: answer = " + answer);
        return answer;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@PathVariable long bookingId, @RequestParam Boolean approved,
                                   @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - changeStatus: bookingId = " + bookingId + ", approved = "
                + approved + ", userId = " + userId);
        ResponseEntity<Object>  answer = bookingClient.changeStatus(bookingId, approved, userId);
        log.debug("+BookingController - changeStatus: answer = " + answer);
        return answer;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable long bookingId, @RequestHeader(HEADER) long userId) {
        log.debug("+BookingController - getById: bookingId = " + bookingId +  ", userId = " + userId);
        ResponseEntity<Object>  answer = bookingClient.getById(bookingId, userId);
        log.debug("+BookingController - getById: answer = " + answer);
        return answer;
    }

    @GetMapping
    public ResponseEntity<Object> getByUserIdAndStateByBooker(
                                        @RequestHeader(HEADER) long userId,
                                        @RequestParam(defaultValue = "ALL") String state,
                                        @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                        @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("+BookingController - getByUserIdAndState: userId = " + userId +  ", state = " + state);
        ResponseEntity<Object>  answer = bookingClient.getByUserIdAndStateByBooker(userId, state, from, size);

        log.info("+BookingController - getByUserIdAndState: answer = " + answer);
        return answer;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByUserIdAndStateByOwner(@RequestHeader(HEADER) long userId,
                                        @RequestParam(defaultValue = "ALL") String state,
                                        @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                        @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.debug("+BookingController - getByOwnerId: userId = " + userId +  ", state = " + state);
        ResponseEntity<Object>  answer = bookingClient.getByUserIdAndStateByOwner(userId, state, from, size);
        log.debug("+BookingController - getByOwnerId: answer = " + answer);
        return answer;
    }
}
