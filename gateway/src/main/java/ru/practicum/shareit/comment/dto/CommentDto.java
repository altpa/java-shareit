package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    long id;
    @NotBlank
    @Size(min = 1, max = 500)
    String text;
    @NotNull
    Item item;
    @NotNull
    @Size(min = 1, max = 255)
    String authorName;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @FutureOrPresent
    LocalDateTime created;
}
