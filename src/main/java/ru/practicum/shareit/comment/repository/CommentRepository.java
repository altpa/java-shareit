package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;

@EnableJpaRepositories
public interface CommentRepository extends Repository<Comment, Long> {
}
