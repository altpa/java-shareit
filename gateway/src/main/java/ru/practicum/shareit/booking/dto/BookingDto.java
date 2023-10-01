package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
public class BookingDto {
    private long id;

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent(groups = Create.class)
    @NotNull(groups = Create.class)
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent(groups = Create.class)
    @NotNull(groups = Create.class)
    private LocalDateTime end;

    private Item item;

    private User booker;

    private BookingStatus status;

    private long itemId;

    private long bookerId;
}
