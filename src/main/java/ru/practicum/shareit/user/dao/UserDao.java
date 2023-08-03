package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.user.exception.UserException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDao implements Dao<User> {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public Optional<User> get(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        checkEmail(user, -1);
        id++;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public User update(User user, int userId) {
        User userForUpdate = ifExist(userId);
        checkEmail(user, userId);
        Optional<String> optionalName = Optional.ofNullable(user.getName());
        Optional<String> optionalEmail = Optional.ofNullable(user.getEmail());

        optionalName.ifPresent(userForUpdate::setName);
        optionalEmail.ifPresent(userForUpdate::setEmail);

        return userForUpdate;
    }

    @Override
    public User delete(int userId) {
        User userForDelete = ifExist(userId);
        users.remove(userId);
        return userForDelete;
    }

    @Override
    public User ifExist(int userId) {
        return get(userId).orElseThrow(() -> {
            throw new UserNotFoundException("Нет юзера с userId = " + userId);
        });
    }

    private void checkEmail(User user, int userId) {
        Optional<String> email = Optional.ofNullable(user.getEmail());
        if (email.isPresent()) {
            for (User u : users.values()) {
                if (u.getEmail().equals(email.get()) && u.getId() != userId) {
                    throw new UserException("Не могу создать юзера: " + user +
                            ". Пользователь с email = " + email.get() + " уже есть");
                }
            }
        }
    }
}
