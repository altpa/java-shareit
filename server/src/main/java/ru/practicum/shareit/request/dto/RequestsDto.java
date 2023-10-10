package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RequestsDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    long id;

    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime created;

    private Set<Item> items;

    private long ownerId;
}