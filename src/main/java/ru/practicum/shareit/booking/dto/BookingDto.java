package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
public class BookingDto {
    private long id;
    private static final String DATE_PATTERN = "YYYY-MM-DD'T'HH:mm:ss";
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @NotNull
    private Date start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent
    @NotNull
    private Date end;
    private long itemId;
    private User booker;
    private BookingStatus status;
}
