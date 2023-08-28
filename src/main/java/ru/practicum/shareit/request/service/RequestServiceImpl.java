package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.RequestsDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private static final RequestMapper mapper = RequestMapper.INSTANCE;

    @Override
    public RequestsDto save(RequestsDto requestsDto, long ownerId) {
        log.info("+RequestServiceImpl - save: " + requestsDto);
        checkOwner(ownerId);
        requestsDto.setCreated(LocalDateTime.now());
        RequestsDto answer = mapper.requestToRequestDto(requestRepository.save(mapper.requestDtoToRequest(requestsDto)));
        log.info("-RequestServiceImpl - save: " + answer);
        return answer;
    }

    @Override
    public List<RequestsDto> getOwnRequests(long ownerId) {
        log.info("+RequestServiceImpl - getOwnRequests");
        checkOwner(ownerId);
        List<RequestsDto> answer = requestRepository.findByOwnerId(ownerId)
                .stream()
                .map(mapper::requestToRequestDto)
                .peek(r -> {
                    Set<Item> items = itemRepository.findByRequestId(r.getId()).toSet();
                    r.setItems(items);
                })
                .sorted(Comparator.comparing(RequestsDto::getCreated))
                .collect(Collectors.toList());

        log.info("-RequestServiceImpl - getOwnRequests: " + answer);
        return answer;
    }

    @Override
    public List<RequestsDto> getAllRequest(int from, int size) {
        log.info("+RequestServiceImpl - getAllRequest: from = " + from + ", size = " + size);
        List<RequestsDto> answer = requestRepository.findAll(PageRequest.of(from, size, Sort.by("created")))
                .stream()
                .map(mapper::requestToRequestDto)
                .collect(Collectors.toList());
        log.info("-RequestServiceImpl - getAllRequest: " + answer);
        return answer;
    }

    @Override
    public RequestsDto getRequestById(long requestId) {
        log.info("+RequestServiceImpl - getRequestById: requestId = " + requestId);
        RequestsDto answer = mapper.requestToRequestDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("requestId = " + requestId + " not found")));
        log.info("-RequestServiceImpl - getRequestById: requestId = " + requestId);
        return answer;
    }

    private void checkOwner(long ownerId) {
        if(!userService.checkOwner(ownerId)) {
            throw new ObjectNotFoundException("userId = " + ownerId + " not found");
        }
    }
}