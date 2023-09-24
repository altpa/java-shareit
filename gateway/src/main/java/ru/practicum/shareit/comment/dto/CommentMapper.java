package ru.practicum.shareit.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.comment.model.Comment;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDto commentToCommentDto(Comment comment);

    Comment commentDtoToComment(CommentDto commentDto);
}
