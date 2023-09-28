package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RequestsDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    long id;

    @NotBlank(message = "description may not be blank", groups = Create.class)
    @Size(min = 1, max = 500)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent(groups = Create.class)
    LocalDateTime created;

    private Set<Item> items;

    @NotNull(message = "ownerId may not be blank")
    private long ownerId;
}