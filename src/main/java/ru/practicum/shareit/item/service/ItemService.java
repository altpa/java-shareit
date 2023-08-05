package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long ownerId);

    List<ItemDto> getAllItems(long ownerId);

    ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId);

    ItemDto getItemById(long itemId);

    List<ItemDto> searchItems(String searchText);
}
