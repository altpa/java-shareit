package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.item.exception.ItemException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemDao implements Dao<Item> {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 0;

    @Override
    public Optional<Item> get(int id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> getAll(int ownerId) {
        List<Item> itemsOfOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == ownerId) {
                itemsOfOwner.add(item);
            }
        }
        return itemsOfOwner;
    }

    @Override
    public Item save(Item item) {
        id++;
        item.setId(id);
        items.put(id, item);
        return item;
    }

    public Item update(Item item, int ownerId, int itemId) {
        Item itemForUpdate = ifExist(itemId);
        Optional<String> optionalName = Optional.ofNullable(item.getName());
        Optional<String> optionalDescription = Optional.ofNullable(item.getDescription());

        if (itemForUpdate.getOwner() == ownerId ) {
            optionalName.ifPresent(itemForUpdate::setName);
            optionalDescription.ifPresent(itemForUpdate::setDescription);
            items.put(itemId, itemForUpdate);
            return itemForUpdate;
        } else {
            throw new ItemNotFoundException("Нельзя обновить item: " + item + ". Неверный владелец: ownerId = " + ownerId +
                    ", а нужен ownerId = " + itemForUpdate.getOwner());
        }
    }

    @Override
    public Item delete(int itemId) {
        Item itemForDelete = ifExist(itemId);
        items.remove(itemId);
        return itemForDelete;
    }

    public List<Item> search(String searchText) {
        List<Item> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            boolean isInName =  item.getName().toLowerCase().contains(searchText.toLowerCase());
            boolean isInDescription = item.getDescription().toLowerCase().contains(searchText.toLowerCase());
            if (!searchText.isEmpty() && (isInName ||isInDescription)) {
                if (item.isAvailable()) {
                    foundItems.add(item);
                }
            }
        }
        return foundItems;
    }

    public void changeItemAvailable(boolean isAvailable, int itemId) {
        Item item = items.get(itemId);
        item.setAvailable(isAvailable);
    }

    @Override
    public Item ifExist(int itemId) {
        return get(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Нет вещи с itemId = " + itemId);
        });
    }
}
