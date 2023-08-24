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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";
    private static final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private static final CommentMapper commentMapper = CommentMapper.INSTANCE;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - addItem: " + itemDto + ". ownerId = " + ownerId);

        ItemDto item = itemMapper.itemToItemDto(itemService.addItem(itemDto, ownerId, userService.checkOwner(ownerId)));
        log.debug("-ItemController - addItem: " + item);
        return item;
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwnerId(@RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - getAllItems: ownerId = " + ownerId);
        List<Item> allItemsByOwnerId = itemService.getAllItemsByOwnerId(ownerId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : allItemsByOwnerId) {
            ItemDto itemDto = itemMapper.itemToItemDto(item);
            itemDto.setComments(itemService.getCommentsToItem(itemDto));
            itemDto.setLastBooking(bookingService.getLastBooking(itemDto, ownerId));
            itemDto.setNextBooking(bookingService.getNextBooking(itemDto, ownerId));
            itemsDto.add(itemDto);
        }
        log.debug("-ItemController - getAllItems: " + allItemsByOwnerId);
        return itemsDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                                    @RequestHeader(HEADER) long ownerId, @PathVariable long itemId) {
        log.debug("+ItemController - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);

            userService.checkOwner(ownerId);

        ItemDto item = itemMapper.itemToItemDto(itemService.updateItem(itemDto, ownerId, itemId));
        log.debug("-ItemController - updateItem: " + item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - getItemById: itemId = " + itemId);
        Item item = itemService.getItemById(itemId, ownerId);

        ItemDto itemDto =
                itemMapper.itemToItemDto(item);

        itemDto.setComments(itemService.getCommentsToItem(itemDto));
        itemDto.setLastBooking(bookingService.getLastBooking(itemDto, ownerId));
        itemDto.setNextBooking(bookingService.getNextBooking(itemDto, ownerId));
        log.debug("-ItemController - getItemById: " + item);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String searchText) {
        log.debug("+ItemController - searchItems: searchText = " + searchText);

        List<ItemDto> items = itemService.searchItems(searchText)
                .stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());

        log.debug("-ItemController - searchItems: " + items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto comment, @PathVariable long itemId,
                              @RequestHeader(HEADER) long ownerId) {
        log.debug("+ItemController - addComment: comment = " + comment + ", ownerId = " + ownerId);
        CommentDto answer = commentMapper.commentToCommentDto(itemService.addComment(comment, ownerId, itemId));
        log.debug("-ItemController - addComment: " + answer);
        return answer;
    }
}