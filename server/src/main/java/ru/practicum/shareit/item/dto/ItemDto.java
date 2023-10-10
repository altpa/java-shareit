package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;
import java.util.List;

@Data
public class ItemDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private LastOrNextBooking lastBooking;

    private LastOrNextBooking nextBooking;

    private List<Comment> comments;

    private long requestId;
}