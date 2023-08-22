package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.Streamable;
import ru.practicum.shareit.comment.model.Comment;

@EnableJpaRepositories
public interface CommentRepository extends Repository<Comment, Long> {
    Comment save(Comment comment);

    Streamable<Comment> findByItemId(long itemId);
}
