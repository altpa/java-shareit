package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private int id;
    @NotBlank(message = "name may not be blank")
    private String name;
    @NotBlank(message = "description may not be blank")
    private String description;
    @NotNull
    private Boolean available;
    private int owner;
    private ItemRequest request;
}
