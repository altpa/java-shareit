package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ItemDto {
    private long id;

    @NotBlank(message = "name may not be blank", groups = Create.class)
    @Size(min = 1, max = 255)
    private String name;

    @NotBlank(message = "description may not be blank", groups = Create.class)
    @Size(min = 1, max = 500)
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;

    private User owner;

    private LastOrNextBooking lastBooking;

    private LastOrNextBooking nextBooking;

    private List<Comment> comments;

    private long requestId;
}