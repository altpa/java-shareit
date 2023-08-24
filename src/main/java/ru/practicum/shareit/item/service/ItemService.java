package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, long ownerId, boolean isOwnerExist);

    List<Item> getAllItemsByOwnerId(long ownerId);

    Item updateItem(ItemDto itemDto, long ownerId, long itemId);

    Item getItemById(long itemId, long ownerId);

    List<Item> searchItems(String searchText);

    Comment addComment(CommentDto comment, long ownerId, long itemId);

    List<Comment> getCommentsToItem(ItemDto item);

}
