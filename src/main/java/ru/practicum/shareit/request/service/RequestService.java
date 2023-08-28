package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestsDto;

import java.util.List;

public interface RequestService {
    RequestsDto save(RequestsDto requestsDto, long ownerId);

    List<RequestsDto> getOwnRequests(long ownerId);

    List<RequestsDto> getAllRequest(int from, int size);

    RequestsDto getRequestById(long requestId);
}
