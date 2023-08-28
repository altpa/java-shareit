package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.Request;

import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface RequestRepository  extends Repository<Request, Long> {
    Request save(Request request);
    Optional<Request> findById(long id);

    Streamable<Request> findByOwnerId(long ownerId);

    Page<Request> findAll(Pageable pageable);
}
