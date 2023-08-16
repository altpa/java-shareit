package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserDao implements Dao<User> {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
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
        emails.add(user.getEmail());
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
            emails.remove(userForUpdate.getEmail());
            emails.add(optionalEmail.get());
            userForUpdate.setEmail(optionalEmail.get());
        }
        return userForUpdate;
    }

    @Override
    public User delete(long userId) {
        User userForDelete = ifExist(userId);
        users.remove(userId);
        emails.remove(userForDelete.getEmail());
        return userForDelete;
    }

    @Override
    public User ifExist(long userId) {
        return get(userId).orElseThrow(() -> new ObjectNotFoundException("Нет юзера с userId = " + userId));
    }

    private void checkEmail(String newEmail, long userId) {
        boolean updateSameEmail = false;
        if (users.containsKey(userId)) {
            Optional<String> oldEmail = Optional.ofNullable(users.get(userId).getEmail());
            updateSameEmail = oldEmail.isPresent() && oldEmail.get().equals(newEmail);
        }

        if (emails.contains(newEmail) ^ updateSameEmail) {
                throw new ObjectsDbException("Не могу создать юзера: userId = " + userId +
                        ". Пользователь с email = " + newEmail + " уже есть");
        }
    }
}
