package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDto {
    private long id;

    @NotBlank(message = "name may not be blank", groups = Marker.OnCreate.class)
    private String name;

    @NotBlank(message = "description may not be blank", groups = Marker.OnCreate.class)
    private String description;

    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;

    private User owner;

    private LastOrNextBooking lastBooking;
    private LastOrNextBooking nextBooking;
    private List<Comment> comments;
}