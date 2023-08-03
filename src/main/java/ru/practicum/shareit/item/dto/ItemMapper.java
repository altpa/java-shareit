package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "available", source = "available", qualifiedByName = "getBoolean")
    ItemDto itemToItemDto(Item item);

    Item itemDtoToItem(ItemDto itemDto);

    @Named("getBoolean")
    default boolean getBoolean(Boolean available) {
            return available;
    }
}