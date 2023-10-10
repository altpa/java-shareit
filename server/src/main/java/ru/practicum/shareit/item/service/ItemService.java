package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long ownerId);

    List<ItemDto> getAllItemsByOwnerId(long ownerId);

    ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId);

    ItemDto getItemById(long itemId, long ownerId);

    List<ItemDto> searchItems(String searchText);

    CommentDto addComment(CommentDto comment, long ownerId, long itemId);
}
