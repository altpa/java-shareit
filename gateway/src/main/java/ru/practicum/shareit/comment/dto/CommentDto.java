package ru.practicum.shareit.comment.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentDto {
    private long id;

    @NotBlank(groups = Create.class)
    @Size(groups = {Create.class, Update.class}, max = 500)
    String text;
}
