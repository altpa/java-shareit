package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastOrNextBooking;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final CommentMapper commentMapper = CommentMapper.INSTANCE;
    private static final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        log.debug("+ItemServiceImpl - addItem: " + itemDto + ". ownerId = " + ownerId);
        itemDto.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() ->  new ObjectNotFoundException("ownerId не найден")));
        ItemDto item = itemMapper.itemToItemDto(itemRepository.save(itemMapper.itemDtoToItem(itemDto)));
        log.debug("-ItemServiceImpl - addItem: " + item);
        return item;
   }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(long ownerId) {
        log.debug("+ItemServiceImpl - getAllItemsByOwnerId: ownerId = " + ownerId);
        List<ItemDto> allItemsByOwnerId = itemRepository.findByOwnerIdOrderByIdAsc(ownerId, PageRequest.of(FROM, SIZE))
                .stream()
                .map(itemMapper::itemToItemDto)
                .collect(toList());

        List<ItemDto> itemsDto = new ArrayList<>();
        allItemsByOwnerId.forEach(i -> {
            itemsDto.add(setCommentsAndBooking(i, ownerId));
        });
        log.debug("-ItemServiceImpl - getAllItemsByOwnerId: " + itemsDto);
        return itemsDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        log.debug("+ItemServiceImpl - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);

        if (userRepository.existsById(ownerId).equals(false)) {
            throw new ObjectNotFoundException("userId = " + ownerId + ", not found");
        }

        ItemDto updatedItem = itemMapper.itemToItemDto(itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectsDbException("Вещи с itemId = " + itemId + " нет")));

        if (updatedItem.getOwner().getId() != ownerId) {
            throw new ObjectsDbException("Нельзя менять владельца, актуальный ownerId = " + updatedItem.getOwner()
                    + ", запрашиваемый ownerId = " + ownerId);
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(updatedItem::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(updatedItem::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(updatedItem::setAvailable);

        updatedItem.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() ->  new ObjectNotFoundException("ownerId не найден")));

        updatedItem = itemMapper.itemToItemDto(itemRepository.save(itemMapper.itemDtoToItem(updatedItem)));

        log.debug("-ItemServiceImpl - updateItem: " + updatedItem);
        return updatedItem;
    }

    @Override
    public ItemDto getItemById(long itemId, long ownerId) {
        log.debug("+ItemServiceImpl - getItemById: itemId = " + itemId);
        ItemDto item = itemMapper.itemToItemDto(itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("itemId = " + itemId + " not found")));
        log.debug("-ItemServiceImpl - getItemById: " + item);
        return setCommentsAndBooking(item, ownerId);
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        log.debug("+ItemServiceImpl - searchItems: searchText = " + searchText);
        if (!searchText.isEmpty()) {
            List<ItemDto> items = itemRepository
                    .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(searchText,
                            searchText, PageRequest.of(FROM, SIZE))
                    .stream()
                    .map(itemMapper::itemToItemDto)
                    .collect(toList());
            log.debug("-ItemServiceImpl - searchItems: " + items);
            return items;
        } else {
            log.debug("-ItemServiceImpl - searchItems: not found");
            return Collections.emptyList();
        }
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        log.debug("+ItemServiceImpl - addComment: comment = " + commentDto + ", ownerId = " + userId + ", itemId = "
                + itemId);
        log.debug("ItemServiceImpl - addComment: " + LocalDateTime.now());

        Optional<List<Booking>> booking = bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, APPROVED,
                        LocalDateTime.now());

        if (booking.get().isEmpty()) {
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
        CommentDto answer = commentMapper.commentToCommentDto(commentRepository.save(comment));
        log.debug("-ItemServiceImpl - addComment: answer = " + answer);
        return answer;
    }

    private List<Comment> getCommentsToItem(ItemDto item) {
        log.debug("+ItemServiceImpl - setCommentsToItem: ItemDtoWithBooking = " + item);
        List<Comment> comments = Collections.emptyList();
        try {
            comments = commentRepository.findByItemId(item.getId())
                    .stream()
                    .collect(toList());
        } catch (InvalidDataAccessResourceUsageException e) {
            log.debug("ItemServiceImpl - setCommentsToItem: no comments");
            throw new ObjectNotFoundException("itemId = " + item.getId() + " no comments");
        }
        log.debug("ItemServiceImpl - setCommentsToItem: comments = " + comments);
        return comments;
    }

    private ItemDto setCommentsAndBooking(ItemDto itemDto, long ownerId) {
        itemDto.setComments(getCommentsToItem(itemDto));
        itemDto.setLastBooking(getLastBooking(itemDto, ownerId));
        itemDto.setNextBooking(getNextBooking(itemDto, ownerId));
        return itemDto;
    }


    private LastOrNextBooking getLastBooking(ItemDto item, long ownerId) {
        boolean isLastBooking = true;
        return getBookingToItem(item, ownerId, isLastBooking);
    }

    private LastOrNextBooking getNextBooking(ItemDto item, long ownerId) {
        boolean isLastBooking = false;
        return getBookingToItem(item, ownerId, isLastBooking);
    }

    private LastOrNextBooking getBookingToItem(ItemDto item, long ownerId, boolean isLastBooking) {
        log.debug("+ItemServiceImpl - getBookingToItem: ItemDtoWithBooking = " + item + ", ownerId = " + ownerId);
        List<LastOrNextBooking> bookings = bookingRepository
                .findByItemIdOrderByStartAsc(item.getId())
                .stream()
                .filter(b -> b.getStatus().equals(APPROVED))
                .collect(Collectors.toList());

        log.debug("ItemServiceImpl - getBookingToItem: bookings = " + bookings
                + ", LocalDateTime.now() = " + LocalDateTime.now());
        LastOrNextBooking answer = null;
        if (item.getOwner().getId() == ownerId && !bookings.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            int lastBookingIndex = 0;
            for (int i = 0; i < bookings.size(); i++) {
                if (bookings.get(i).getStart().isBefore(now)) {
                    lastBookingIndex = i;
                }
            }
            int nextBookingIndex = lastBookingIndex + 1;
            if (isLastBooking) {
                if (bookings.get(lastBookingIndex).getStart().isBefore(now)) {
                    answer = bookings.get(lastBookingIndex);
                }
            } else if (bookings.size() > nextBookingIndex) {
                answer = bookings.get(nextBookingIndex);
            }
        }
        log.debug("-ItemServiceImpl - getBookingToItem: ItemDtoWithBooking = " + item);
        return answer;
    }
}