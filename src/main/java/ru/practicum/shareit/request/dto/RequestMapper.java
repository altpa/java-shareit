package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.Request;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    RequestsDto requestToRequestDto(Request request);

    Request requestDtoToRequest(RequestsDto requestsDto);
}
