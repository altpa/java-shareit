package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    List<ItemDto> getAllItems(int ownerId);

    ItemDto updateItem(ItemDto itemDto, int ownerId, int itemId);

    ItemDto getItemById(int itemId);

    List<ItemDto> searchItems(String searchText);
}
