package ru.practicum.shareit.dao;

import java.util.Optional;

public interface Dao<E> {
    Optional<E> get(int id);
    E save(E t);
    E delete(int id);
    E ifExist(int id);
}