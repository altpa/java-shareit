package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - addItem: " + itemDto + ". ownerId = " + ownerId);
        ResponseEntity<Object> item = itemClient.addItem(itemDto, ownerId);
        log.debug("-ItemController - addItem: " + item);
        return item;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - getAllItemsByOwnerId: ownerId = " + ownerId);
        ResponseEntity<Object> allItemsByOwnerId = itemClient.getAllItemsByOwnerId(ownerId);
        log.debug("-ItemController - getAllItemsByOwnerId: " + allItemsByOwnerId);
        return allItemsByOwnerId;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                    @RequestHeader(HEADER) long ownerId, @PathVariable long itemId) {
        log.debug("+ItemController - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);
        ResponseEntity<Object> item = itemClient.updateItem(itemDto, ownerId, itemId);
        log.debug("-ItemController - updateItem: " + item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId, @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - getItemById: itemId = " + itemId);
        ResponseEntity<Object> item = itemClient.getItemById(itemId, ownerId);
        log.debug("-ItemController - getItemById: " + item);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String searchText) {
        log.debug("+ItemController - searchItems: searchText = " + searchText);
        ResponseEntity<Object> items = itemClient.searchItems(searchText);
        log.debug("-ItemController - searchItems: " + items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto comment, @PathVariable long itemId,
                              @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - addComment: comment = " + comment + ", ownerId = " + ownerId);
        ResponseEntity<Object> answer = itemClient.addComment(comment, ownerId, itemId);
        log.debug("-ItemController - addComment: " + answer);
        return answer;
    }
}