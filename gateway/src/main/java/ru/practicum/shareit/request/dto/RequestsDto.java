package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RequestsDto {
    private long id;

    @NotBlank(message = "description may not be blank", groups = Create.class)
    @Size(groups = Create.class, max = 500)
    private String description;
}