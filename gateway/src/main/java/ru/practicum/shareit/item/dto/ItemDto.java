package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemDto {
    private long id;

    @NotBlank(message = "name may not be blank", groups = Create.class)
    @Size(groups = {Create.class, Update.class}, max = 255)
    private String name;

    @NotBlank(message = "description may not be blank", groups = Create.class)
    @Size(groups = {Create.class, Update.class}, max = 500)
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;

    private long requestId;
}