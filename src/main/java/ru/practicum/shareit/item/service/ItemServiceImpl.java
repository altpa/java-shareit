package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao = new ItemDao();
    private static final ItemMapper mapper = ItemMapper.INSTANCE;

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        log.info("+addItem: " + itemDto + ". ownerId = " + ownerId);
        if (ownerId != 0) {
            itemDto.setOwner(ownerId);
            ItemDto item = mapper.itemToItemDto(itemDao.save(mapper.itemDtoToItem(itemDto)));
            log.info("-addItem: " + item);
            return item;
        } else {
            throw new ItemException("ownerId не может быть 0");
        }
    }

    @Override
    public List<ItemDto> getAllItems(int ownerId) {
        log.info("+getAllItems: ownerId = " + ownerId);
        List<ItemDto> items = itemDao.getAll(ownerId)
                .stream()
                .map(mapper::itemToItemDto)
                .collect(toList());
        log.info("-getAllItems: " + items);
        return items;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int ownerId, int itemId) {
        log.info("+updateItem: " + itemDto + ". ownerId = " + ownerId + ". itemId = " + itemId);
        ItemDto updatedDto =
                mapper.itemToItemDto(itemDao.update(mapper.itemDtoToItem(itemDto), ownerId, itemId));
        if (itemDto.getAvailable() != null) {
            itemDao.changeItemAvailable(itemDto.getAvailable(), itemId);
            updatedDto.setAvailable(itemDto.getAvailable());
        }
        log.info("-updateItem: " + updatedDto);
        return updatedDto;
    }

    @Override
    public ItemDto getItemById(int itemId) {
        log.info("+getItemById: itemId = " + itemId);
        Optional<Item> itemOptional = Optional.ofNullable(itemDao.get(itemId)).get();
        ItemDto item = mapper.itemToItemDto(itemOptional.orElseThrow(
                () -> new ItemException("Вещи с itemId = " + itemId + " нет")));
        log.info("-getItemById: " + item);
        return item;
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        log.info("+searchItems: searchText = " + searchText);
        List<ItemDto> items = itemDao.search(searchText)
                .stream()
                .map(mapper::itemToItemDto)
                .collect(toList());
        log.info("-searchItems: " + items);
        return items;
    }
}