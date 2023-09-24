package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LastOrNextBooking {
    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private long bookerId;

    private BookingStatus status;
}
