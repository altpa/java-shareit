package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    long id;
    @NotBlank
    String text;
    @NotNull(groups = Marker.OnCreate.class)
    Item item;
    @NotNull(groups = Marker.OnCreate.class)
    String authorName;
    @NotNull(groups = Marker.OnCreate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent
    LocalDateTime created;
}
