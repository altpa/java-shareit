package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface UserRepository extends Repository<User, Long> {
    User save(User user);

    Optional<User> findById(Long userId);

    Iterable<User> findAll();

    User deleteById(Long id);

    Boolean existsById(Long userId);
}
