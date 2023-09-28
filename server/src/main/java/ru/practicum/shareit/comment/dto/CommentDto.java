package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    long id;
    String text;
    Item item;
    String authorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime created;
}
