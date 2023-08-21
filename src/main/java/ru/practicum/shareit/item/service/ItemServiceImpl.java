package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastBooking;
import ru.practicum.shareit.booking.model.NextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoById;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private static final ItemMapper mapper = ItemMapper.INSTANCE;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId, boolean isOwnerExist) {
        log.info("+ItemServiceImpl - addItem: " + itemDto + ". ownerId = " + ownerId);
        itemDto.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() ->  new ObjectNotFoundException("ownerId не найден")));
        ItemDto item = mapper.itemToItemDto(itemRepository.save(mapper.itemDtoToItem(itemDto)));
        log.info("-ItemServiceImpl - addItem: " + item);
        return item;

   }

    @Override
    public List<ItemDtoById> getAllItemsByOwnerId(long ownerId) {
        log.info("+ItemServiceImpl - getAllItems: ownerId = " + ownerId);
        List<ItemDtoById> items = itemRepository.findByOwnerId(ownerId)
                .stream()
                .map(mapper::itemToItemDtoById)
                .map(i -> setBookingToItem(i, ownerId))
                .collect(toList());
        log.info("-ItemServiceImpl - getAllItems: " + items);
        return items;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        log.info("+ItemServiceImpl - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);

        ItemDto updatedDto = mapper.itemToItemDto(itemRepository.findById(itemId).orElseThrow(
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

        updatedDto = mapper.itemToItemDto(itemRepository.save(mapper.itemDtoToItem(updatedDto)));

        log.info("-ItemServiceImpl - updateItem: " + updatedDto);
        return updatedDto;
    }

    @Override
    public ItemDtoById getItemById(long itemId, long ownerId) {
        log.info("+ItemServiceImpl - getItemById: itemId = " + itemId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        ItemDtoById item = mapper.itemToItemDtoById(itemOptional.orElseThrow(
                () -> new ObjectNotFoundException("Вещи с itemId = " + itemId + " нет")));
        log.info("-ItemServiceImpl - getItemById: " + item);
        Streamable<Booking> bookings = bookingRepository.findFirst2ByItemIdOrderByStartAsc(itemId);

        return setBookingToItem(item, ownerId);
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        log.info("+ItemServiceImpl - searchItems: searchText = " + searchText);
        if (!searchText.isEmpty()) {
            List<ItemDto> items = itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText)
                    .stream()
                    .map(mapper::itemToItemDto)
                    .collect(toList());
            log.info("-ItemServiceImpl - searchItems: " + items);
            return items;
        } else {
            log.info("-ItemServiceImpl - searchItems: not found");
            return List.of();
        }
    }

    @Override
    public CommentDto addComment(CommentDto comment, long ownerId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Вещи с itemId = " + itemId + " нет"));
        return null;
    }

    private ItemDtoById setBookingToItem(ItemDtoById item, long ownerId) {
        List<Booking> bookings = bookingRepository.findFirst2ByItemIdOrderByStartAsc(item.getId()).toList();
        if (item.getOwner().getId() == ownerId && bookings.size() != 0) {
            item.setLastBooking(new LastBooking(bookings.get(0).getId(), bookings.get(0).getStart(),
                    bookings.get(0).getEnd(), bookings.get(0).getBooker().getId(), bookings.get(0).getStatus()));
            item.setNextBooking(new NextBooking(bookings.get(1).getId(), bookings.get(1).getStart(),
                    bookings.get(1).getEnd(), bookings.get(1).getBooker().getId(), bookings.get(1).getStatus()));
        }
        return item;
    }
}