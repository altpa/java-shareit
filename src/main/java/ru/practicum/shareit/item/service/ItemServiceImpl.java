package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private static final ItemMapper mapper = ItemMapper.INSTANCE;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        log.info("+ItemServiceImpl - addItem: " + itemDto + ". ownerId = " + ownerId);
        if (ownerId != 0) {
            itemDto.setOwner(ownerId);
            ItemDto item = mapper.itemToItemDto(itemDao.save(mapper.itemDtoToItem(itemDto)));
            log.info("-ItemServiceImpl - addItem: " + item);
            return item;
        } else {
            throw new ObjectsDbException("ownerId не может быть 0");
        }
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        log.info("+ItemServiceImpl - getAllItems: ownerId = " + ownerId);
        List<ItemDto> items = itemDao.getAll(ownerId)
                .stream()
                .map(mapper::itemToItemDto)
                .collect(toList());
        log.info("-ItemServiceImpl - getAllItems: " + items);
        return items;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        log.info("+ItemServiceImpl - updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);
        ItemDto updatedDto =
                mapper.itemToItemDto(itemDao.update(mapper.itemDtoToItem(itemDto), ownerId, itemId));
        if (itemDto.getAvailable() != null) {
            itemDao.changeItemAvailable(itemDto.getAvailable(), itemId);
            updatedDto.setAvailable(itemDto.getAvailable());
        }
        log.info("-ItemServiceImpl - updateItem: " + updatedDto);
        return updatedDto;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("+ItemServiceImpl - getItemById: itemId = " + itemId);
        Optional<Item> itemOptional = itemDao.get(itemId);
        ItemDto item = mapper.itemToItemDto(itemOptional.orElseThrow(
                () -> new ObjectsDbException("Вещи с itemId = " + itemId + " нет")));
        log.info("-ItemServiceImpl - getItemById: " + item);
        return item;
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        log.info("+ItemServiceImpl - searchItems: searchText = " + searchText);
        List<ItemDto> items = itemDao.search(searchText)
                .stream()
                .map(mapper::itemToItemDto)
                .collect(toList());
        log.info("-ItemServiceImpl - searchItems: " + items);
        return items;
    }
}