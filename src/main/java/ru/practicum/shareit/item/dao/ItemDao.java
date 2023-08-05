package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ItemDao implements Dao<Item> {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> usersItems = new HashMap<>();
    private long id = 0;

    @Override
    public Optional<Item> get(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> getAll(long ownerId) {
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
        Set<Long> itemsOfUser = new HashSet<>();
        itemsOfUser.add(id);
        usersItems.put(item.getOwner(), itemsOfUser);
        return item;
    }

    public Item update(Item item, long ownerId, long itemId) {
        Item itemForUpdate = ifExist(itemId);
        Optional<String> optionalName = Optional.ofNullable(item.getName());
        Optional<String> optionalDescription = Optional.ofNullable(item.getDescription());
        Optional<Boolean> optionalIsAvailable = Optional.ofNullable(item.getAvailable());
        if (itemForUpdate.getOwner() == ownerId) {
            optionalName.ifPresent(itemForUpdate::setName);
            optionalDescription.ifPresent(itemForUpdate::setDescription);
            optionalIsAvailable.ifPresent(itemForUpdate::setAvailable);
            items.put(itemId, itemForUpdate);
            Set<Long> itemsOfUser = usersItems.get(ownerId);
            itemsOfUser.add(itemId);
            usersItems.put(ownerId, itemsOfUser);
            return itemForUpdate;
        } else {
            throw new ObjectNotFoundException("Нельзя обновить item: " + item + ". Неверный владелец: ownerId = " +
                    ownerId + ", а нужен ownerId = " + itemForUpdate.getOwner());
        }
    }

    @Override
    public Item delete(long itemId) {
        Item itemForDelete = ifExist(itemId);
        long ownerId = itemForDelete.getOwner();
        items.remove(itemId);
        Set<Long> itemsOfUser = usersItems.get(ownerId);
        itemsOfUser.remove(itemId);
        usersItems.put(ownerId, itemsOfUser);
        return itemForDelete;
    }

    public List<Item> search(String searchText) {
        String searchTextToLowerCase = searchText.toLowerCase();
        List<Item> foundItems = new ArrayList<>();
        if (!searchText.isEmpty()) {
            foundItems = items.values().stream().filter(item ->
                    (item.getName().toLowerCase().contains(searchTextToLowerCase) ||
                    item.getDescription().toLowerCase().contains(searchTextToLowerCase)) &&
                    item.getAvailable())
                .collect(Collectors.toList());
        }
        return foundItems;
    }

    public void changeItemAvailable(boolean isAvailable, long itemId) {
        Item item = items.get(itemId);
        item.setAvailable(isAvailable);
    }

    @Override
    public Item ifExist(long itemId) {
        return get(itemId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Нет вещи с itemId = " + itemId);
        });
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
