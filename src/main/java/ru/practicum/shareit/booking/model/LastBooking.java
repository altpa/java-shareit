package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
public class LastBooking {
    private long id;

    private Instant start;

    private Instant end;

    private long bookerId;

    private BookingStatus status;
}
