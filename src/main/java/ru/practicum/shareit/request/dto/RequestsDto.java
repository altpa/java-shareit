package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RequestsDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    long id;

    @NotBlank(message = "description may not be blank")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent
//    @NotBlank(message = "created may not be blank", groups = Marker.OnCreate.class)
    LocalDateTime created;

    private Set<Item> items;

    @NotNull(message = "ownerId may not be blank")
    private long ownerId;
}