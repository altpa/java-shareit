package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.LastBooking;
import ru.practicum.shareit.booking.model.NextBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDtoById {
    private long id;

    @NotBlank(message = "name may not be blank", groups = Marker.OnCreate.class)
    private String name;

    @NotBlank(message = "description may not be blank", groups = Marker.OnCreate.class)
    private String description;

    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;

    private User owner;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
}