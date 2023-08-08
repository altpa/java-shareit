package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

public class BookingDto {
    private long id;
    private static final String DATE_PATTERN = "dd-MM-yyyy@HH:mm:ss";
    @JsonFormat(pattern = DATE_PATTERN)
    @PastOrPresent
    private LocalDateTime start;
    @JsonFormat(pattern = DATE_PATTERN)
    @Future
    private LocalDateTime end;
    private long item;
    private long booker;
    private BookingStatus status;
}
