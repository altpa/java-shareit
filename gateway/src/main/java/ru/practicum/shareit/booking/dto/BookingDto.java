package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.StartBeforeOrEqualEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@StartBeforeOrEqualEndDateValid(groups = Create.class)
public class BookingDto {
    private long id;

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime end;

    private long itemId;
}
