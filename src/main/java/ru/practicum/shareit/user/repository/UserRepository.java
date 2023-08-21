package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface UserRepository extends Repository<User, Long> {
    User save(User user);

    Optional<User> findById(Long primaryKey);

    Iterable<User> findAll();

    long count();

    void delete(User user);

    User deleteById(Long id);

    Boolean existsById(Long primaryKey);

    @Modifying(clearAutomatically = true)
    @Query("update User updatedUser set updatedUser.name = :name, updatedUser.email = :email where updatedUser.id = :userId")
    int update(@Param(value = "name") String name, @Param(value = "email") String email, @Param(value = "userId") Long userId);
}
