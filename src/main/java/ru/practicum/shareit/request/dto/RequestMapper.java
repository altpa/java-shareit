package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest);

    ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto);
}