package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

public class BookingDto {
    private long id;
    @JsonFormat(pattern = "dd-MM-yyyy@HH:mm:ss")
    @PastOrPresent
    private LocalDateTime start;
    @JsonFormat(pattern = "dd-MM-yyyy@HH:mm:ss")
    @Future
    private LocalDateTime end;
    private long item;
    private long booker;
    private BookingStatus status;
}
