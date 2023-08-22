package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
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
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final BookingStatus APPROVED = BookingStatus.APPROVED;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private static final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private static final CommentMapper commentMapper = CommentMapper.INSTANCE;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId, boolean isOwnerExist) {
        log.info("+ItemServiceImpl - addItem: " + itemDto + ". ownerId = " + ownerId);
        itemDto.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() ->  new ObjectNotFoundException("ownerId не найден")));
        ItemDto item = itemMapper.itemToItemDto(itemRepository.save(itemMapper.itemDtoToItem(itemDto)));
        log.info("-ItemServiceImpl - addItem: " + item);
        return item;

   }

    @Override
    public List<ItemDtoWithBookingAndComments> getAllItemsByOwnerId(long ownerId) {
        log.info("+ItemServiceImpl - getAllItems: ownerId = " + ownerId);
        List<ItemDtoWithBookingAndComments> items = itemRepository.findByOwnerId(ownerId)
                .stream()
                .map(itemMapper::itemToItemDtoById)
                .map(i -> setBookingToItem(i, ownerId))
                .map(this::setCommentsToItem)
                .collect(toList());
        log.info("-ItemServiceImpl - getAllItems: " + items);
        return items;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        log.info("+ItemServiceImpl - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);

        ItemDto updatedDto = itemMapper.itemToItemDto(itemRepository.findById(itemId).orElseThrow(
                    () -> new ObjectsDbException("Вещи с itemId = " + itemId + " нет")));

        if (updatedDto.getOwner().getId() != ownerId) {
            throw new ObjectsDbException("Нельзя менять владельца, актуальный ownerId = " + updatedDto.getOwner()
                    + ", запрашиваемый ownerId = " + ownerId);
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(updatedDto::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(updatedDto::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(updatedDto::setAvailable);

        updatedDto.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() ->  new ObjectNotFoundException("ownerId не найден")));

        updatedDto = itemMapper.itemToItemDto(itemRepository.save(itemMapper.itemDtoToItem(updatedDto)));

        log.info("-ItemServiceImpl - updateItem: " + updatedDto);
        return updatedDto;
    }

    @Override
    public ItemDtoWithBookingAndComments getItemById(long itemId, long ownerId) {
        log.info("+ItemServiceImpl - getItemById: itemId = " + itemId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        ItemDtoWithBookingAndComments item = itemMapper.itemToItemDtoById(itemOptional.orElseThrow(
                () -> new ObjectNotFoundException("itemId = " + itemId + " not found")));
        setBookingToItem(item, ownerId);
        setCommentsToItem(item);
        log.info("-ItemServiceImpl - getItemById: " + item);
        return item;
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        log.info("+ItemServiceImpl - searchItems: searchText = " + searchText);
        if (!searchText.isEmpty()) {
            List<ItemDto> items = itemRepository
                    .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText)
                    .stream()
                    .map(itemMapper::itemToItemDto)
                    .collect(toList());
            log.info("-ItemServiceImpl - searchItems: " + items);
            return items;
        } else {
            log.info("-ItemServiceImpl - searchItems: not found");
            return List.of();
        }
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        log.info("+ItemServiceImpl - addComment: comment = " + commentDto + ", ownerId = " + userId + ", itemId = "
                + itemId);
        log.info("ItemServiceImpl - addComment: " + LocalDateTime.now());

        List<Booking> booking = bookingRepository
                .findByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, APPROVED,
                        LocalDateTime.now()).get();

        if (booking.isEmpty()) {
            throw new BadRequestException("userId = " + userId + " not booked itemId = "
                    + itemId);
        }
        log.info("ItemServiceImpl - addComment: booking = " + booking);

        Comment comment = commentMapper.commentDtoToComment(commentDto);

        comment.setText(comment.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() ->  new ObjectNotFoundException("itemId = " + itemId + " not found")));

        User author = userRepository.findById(userId)
                .orElseThrow(() ->  new ObjectNotFoundException("userId = " + userId + " not found"));
        comment.setAuthorName(author.getName());

        log.info("ItemServiceImpl - addComment: comment = " + comment);
        CommentDto answer = commentMapper.commentToCommentDto(commentRepository.save(comment));
        log.info("-ItemServiceImpl - addComment: answer = " + answer);
        return answer;
    }

    private ItemDtoWithBookingAndComments setBookingToItem(ItemDtoWithBookingAndComments item, long ownerId) {
        log.info("+ItemServiceImpl - setBookingToItem: ItemDtoWithBooking = " + item + ", ownerId = " + ownerId);
        List<Booking> bookings = bookingRepository
                .findByItemIdOrderByStartAsc(item.getId())
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList());

        log.info("ItemServiceImpl - setBookingToItem: bookings = " + bookings + ", LocalDateTime.now() = " + LocalDateTime.now());
        if (item.getOwner().getId() == ownerId && !bookings.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            int lastBookingIndex = 0;
            for (int i = 0; i < bookings.size(); i++) {
                if (bookings.get(i).getStart().isBefore(now)) {
                    lastBookingIndex = i;
                }
            }
            int nextBookingIndex = lastBookingIndex + 1;

            item.setLastBooking(createLastOrNextBooking(bookings.get(lastBookingIndex)));
            if (bookings.size() > nextBookingIndex) {
                item.setNextBooking(createLastOrNextBooking(bookings.get(nextBookingIndex)));
            }
        }

        log.info("-ItemServiceImpl - setBookingToItem: ItemDtoWithBooking = " + item);
        return item;
    }

    private ItemDtoWithBookingAndComments setCommentsToItem(ItemDtoWithBookingAndComments item) {
        log.info("+ItemServiceImpl - setCommentsToItem: ItemDtoWithBooking = " + item);
        List<Comment> comments = Collections.emptyList();
        try {
            comments = commentRepository.findByItemId(item.getId()).toList();
        } catch (InvalidDataAccessResourceUsageException e) {
            log.info("ItemServiceImpl - setCommentsToItem: no comments");
        }
        log.info("ItemServiceImpl - setCommentsToItem: comments = " + comments);
            item.setComments(comments);
        log.info("-ItemServiceImpl - setCommentsToItem: item = " + item);
        return item;
    }

    private LastOrNextBooking createLastOrNextBooking(Booking booking) {
        return new LastOrNextBooking(booking.getId(), booking.getStart(),
                booking.getEnd(), booking.getBooker().getId(), booking.getStatus());
    }
}