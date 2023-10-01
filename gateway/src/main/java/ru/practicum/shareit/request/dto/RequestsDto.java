package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestsDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private long id;

    @NotBlank(message = "description may not be blank", groups = Create.class)
    @Size(min = 1, max = 500)
    private String description;

    @NotNull(message = "ownerId may not be blank")
    private long ownerId;
}