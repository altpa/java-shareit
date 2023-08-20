package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER) long ownerId) {
        log.info("+ItemController - addItem: " + itemDto + ". ownerId = " + ownerId);

        ItemDto item = itemService.addItem(itemDto, ownerId, userService.checkOwner(ownerId));
        log.info("-ItemController - addItem: " + item);
        return item;
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(HEADER) long ownerId) {
        log.info("+ItemController - getAllItems: ownerId = " + ownerId);
        List<ItemDto> allItems = itemService.getAllItems(ownerId);
        log.info("-ItemController - getAllItems: " + allItems);
        return allItems;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                                    @RequestHeader(HEADER) long ownerId, @PathVariable long itemId) {
        log.info("+ItemController - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);

            userService.checkOwner(ownerId);

        ItemDto item = itemService.updateItem(itemDto, ownerId, itemId);
        log.info("-ItemController - updateItem: " + item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("+ItemController - getItemById: itemId = " + itemId);
        ItemDto item = itemService.getItemById(itemId);
        log.info("-ItemController - getItemById: " + item);
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String searchText) {
        log.info("+ItemController - searchItems: searchText = " + searchText);
        List<ItemDto> items = itemService.searchItems(searchText);
        log.info("-ItemController - searchItems: " + items);
        return items;
    }
}