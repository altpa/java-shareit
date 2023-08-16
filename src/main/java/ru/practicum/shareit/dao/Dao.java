package ru.practicum.shareit.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<E> {
    Optional<E> get(long id);

    E save(E e);

    E delete(long id);

    E ifExist(long id);

    List<E> getAll();
}