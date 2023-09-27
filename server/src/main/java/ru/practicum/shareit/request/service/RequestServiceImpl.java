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
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final RequestMapper mapper = RequestMapper.INSTANCE;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public RequestsDto save(RequestsDto requestsDto, long ownerId) {
        log.info("+RequestServiceImpl - save: " + requestsDto);
        checkUser(ownerId);
        requestsDto.setCreated(LocalDateTime.now());
        requestsDto.setOwnerId(ownerId);
        RequestsDto answer = mapper.requestToRequestDto(requestRepository.save(mapper.requestDtoToRequest(requestsDto)));
        log.info("-RequestServiceImpl - save: " + answer);
        return answer;
    }

    @Override
    public List<RequestsDto> getOwnRequests(long ownerId) {
        log.info("+RequestServiceImpl - getOwnRequests");
        checkUser(ownerId);
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
    public List<RequestsDto> getAllRequest(int from, int size, long userId) {
        checkUser(userId);
        log.info("+RequestServiceImpl - getAllRequest: from = " + from + ", size = " + size);
        List<RequestsDto> answer = requestRepository.findAll(PageRequest.of(from, size, Sort.by("created")))
                .stream()
                .map(mapper::requestToRequestDto)
                .peek(r -> {
                    Set<Item> items = itemRepository.findByOwnerIdAndRequestId(userId, r.getId()).toSet();
                    r.setItems(items);
                })
                .filter(r -> !r.getItems().isEmpty())
                .collect(Collectors.toList());
        log.info("-RequestServiceImpl - getAllRequest: " + answer);
        return answer;
    }

    @Override
    public RequestsDto getRequestById(long requestId, long userId) {
        checkUser(userId);
        log.info("+RequestServiceImpl - getRequestById: requestId = " + requestId);
        RequestsDto answer = mapper.requestToRequestDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("requestId = " + requestId + " not found")));
        Set<Item> items = itemRepository.findByRequestId(answer.getId()).toSet();
        answer.setItems(items);
        log.info("-RequestServiceImpl - getRequestById: requestId = " + requestId);
        return answer;
    }

    private void checkUser(long ownerId) {
        if (userRepository.existsById(ownerId).equals(false)) {
            throw new ObjectNotFoundException("userId = " + ownerId + " not found");
        }
    }
}
