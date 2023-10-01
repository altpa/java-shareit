package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - addItem: {}. ownerId = {}", itemDto, ownerId);
        ItemDto item = itemService.addItem(itemDto, ownerId);
        log.debug("-ItemController - addItem: {}", item);
        return item;
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwnerId(@RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - getAllItemsByOwnerId: ownerId = {}", ownerId);
        List<ItemDto> allItemsByOwnerId = itemService.getAllItemsByOwnerId(ownerId);
        log.debug("-ItemController - getAllItemsByOwnerId: {}", allItemsByOwnerId);
        return allItemsByOwnerId;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                                    @RequestHeader(HEADER) long ownerId, @PathVariable long itemId) {
        log.debug("+ItemController - updateItem: {}. ownerId = {}. itemId = {}", itemDto, ownerId, itemId);
        ItemDto item = itemService.updateItem(itemDto, ownerId, itemId);
        log.debug("-ItemController - updateItem: {}", item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - getItemById: itemId = {}", itemId);
        ItemDto item = itemService.getItemById(itemId, ownerId);
        log.debug("-ItemController - getItemById: {}", item);
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String searchText) {
        log.debug("+ItemController - searchItems: searchText = {}", searchText);
        List<ItemDto> items = itemService.searchItems(searchText);
        log.debug("-ItemController - searchItems: {}", items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto comment, @PathVariable long itemId,
                              @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - addComment: comment = {}, ownerId = {}", comment, ownerId);
        CommentDto answer = itemService.addComment(comment, ownerId, itemId);
        log.debug("-ItemController - addComment: {}", answer);
        return answer;
    }
}