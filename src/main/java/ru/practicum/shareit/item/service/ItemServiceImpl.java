package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private static final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private static final CommentMapper commentMapper = CommentMapper.INSTANCE;

    @Override
    public Item addItem(ItemDto itemDto, long ownerId, boolean isOwnerExist) {
        log.debug("+ItemServiceImpl - addItem: " + itemDto + ". ownerId = " + ownerId);
        itemDto.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() ->  new ObjectNotFoundException("ownerId не найден")));
        Item item = itemRepository.save(itemMapper.itemDtoToItem(itemDto));
        log.debug("-ItemServiceImpl - addItem: " + item);
        return item;

   }

    @Override
    public List<Item> getAllItemsByOwnerId(long ownerId) {
        log.debug("+ItemServiceImpl - getAllItems: ownerId = " + ownerId);
        List<Item> items = itemRepository.findByOwnerId(ownerId)
                .stream()
                .collect(toList());
//        List<ItemDtoWithBookingAndComments> items = itemRepository.findByOwnerId(ownerId)
//                .stream()
//                .map(itemMapper::itemToItemDtoById)
//                .map(i -> setBookingToItem(i, ownerId))
//                .map(this::setCommentsToItem)
//                .collect(toList());
        log.debug("-ItemServiceImpl - getAllItems: " + items);
        return items;
    }

    @Override
    public Item updateItem(ItemDto itemDto, long ownerId, long itemId) {
        log.debug("+ItemServiceImpl - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);

        Item updatedItem = itemRepository.findById(itemId).orElseThrow(
                    () -> new ObjectsDbException("Вещи с itemId = " + itemId + " нет"));

        if (updatedItem.getOwner().getId() != ownerId) {
            throw new ObjectsDbException("Нельзя менять владельца, актуальный ownerId = " + updatedItem.getOwner()
                    + ", запрашиваемый ownerId = " + ownerId);
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(updatedItem::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(updatedItem::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(updatedItem::setAvailable);

        updatedItem.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() ->  new ObjectNotFoundException("ownerId не найден")));

        updatedItem = itemRepository.save(updatedItem);

        log.debug("-ItemServiceImpl - updateItem: " + updatedItem);
        return updatedItem;
    }

    @Override
    public Item getItemById(long itemId, long ownerId) {
        log.debug("+ItemServiceImpl - getItemById: itemId = " + itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("itemId = " + itemId + " not found"));
        log.debug("-ItemServiceImpl - getItemById: " + item);
        return item;
    }

    @Override
    public List<Item> searchItems(String searchText) {
        log.debug("+ItemServiceImpl - searchItems: searchText = " + searchText);
        if (!searchText.isEmpty()) {
            List<Item> items = itemRepository
                    .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText)
                    .stream()
                    .collect(toList());
            log.debug("-ItemServiceImpl - searchItems: " + items);
            return items;
        } else {
            log.debug("-ItemServiceImpl - searchItems: not found");
            return List.of();
        }
    }

    @Override
    public Comment addComment(CommentDto commentDto, long userId, long itemId) {
        log.debug("+ItemServiceImpl - addComment: comment = " + commentDto + ", ownerId = " + userId + ", itemId = "
                + itemId);
        log.debug("ItemServiceImpl - addComment: " + LocalDateTime.now());

        List<Booking> booking = bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, APPROVED,
                        LocalDateTime.now()).get();

        if (booking.isEmpty()) {
            throw new BadRequestException("userId = " + userId + " not booked itemId = "
                    + itemId);
        }
        log.debug("ItemServiceImpl - addComment: booking = " + booking);

        Comment comment = commentMapper.commentDtoToComment(commentDto);

        comment.setText(comment.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() ->  new ObjectNotFoundException("itemId = " + itemId + " not found")));

        User author = userRepository.findById(userId)
                .orElseThrow(() ->  new ObjectNotFoundException("userId = " + userId + " not found"));
        comment.setAuthorName(author.getName());

        log.debug("ItemServiceImpl - addComment: comment = " + comment);
        Comment answer = commentRepository.save(comment);
        log.debug("-ItemServiceImpl - addComment: answer = " + answer);
        return answer;
    }

    @Override
    public List<Comment> getCommentsToItem(ItemDto item) {
        log.debug("+ItemServiceImpl - setCommentsToItem: ItemDtoWithBooking = " + item);
        List<Comment> comments = Collections.emptyList();
        try {
            comments = commentRepository.findByItemId(item.getId()).toList();
        } catch (InvalidDataAccessResourceUsageException e) {
            log.debug("ItemServiceImpl - setCommentsToItem: no comments");
        }
        log.debug("ItemServiceImpl - setCommentsToItem: comments = " + comments);
        return comments;
    }


}