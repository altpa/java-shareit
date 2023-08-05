package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDao implements Dao<User> {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();
    private long id = 0;

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        checkEmail(user.getEmail(), id + 1);
        id++;
        user.setId(id);
        users.put(id, user);
        emails.put(id, user.getEmail());
        return user;
    }

    public User update(User user, long userId) {
        User userForUpdate = ifExist(userId);
        Optional<String> optionalName = Optional.ofNullable(user.getName());
        Optional<String> optionalEmail = Optional.ofNullable(user.getEmail());
        if (optionalEmail.isPresent()) {
            checkEmail(user.getEmail(), userId);
        }

        optionalName.ifPresent(userForUpdate::setName);
        if (optionalEmail.isPresent()) {
            emails.remove(userId);
            emails.put(userForUpdate.getId(), optionalEmail.get());
            userForUpdate.setEmail(optionalEmail.get());
        }
        return userForUpdate;
    }

    @Override
    public User delete(long userId) {
        User userForDelete = ifExist(userId);
        users.remove(userId);
        emails.remove(userId);
        return userForDelete;
    }

    @Override
    public User ifExist(long userId) {
        return get(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Нет юзера с userId = " + userId);
        });
    }

    private void checkEmail(String newEmail, long userId) {
        Optional<String> oldEmail = Optional.ofNullable(emails.get(userId));
        if (emails.containsValue(newEmail) &&
                !(oldEmail.isPresent() && oldEmail.get().equals(newEmail))) {
            throw new ObjectsDbException("Не могу создать юзера: userId = " + userId +
                    ". Пользователь с email = " + newEmail + " уже есть");
        }
    }
}
