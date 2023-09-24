package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestsDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final RequestService requestService;

    @PostMapping
    RequestsDto addRequest(@RequestBody RequestsDto requestsDto, @RequestHeader(HEADER) long ownerId) {
        log.info("+requestController - addRequest: " + requestsDto);
        RequestsDto answer =  requestService.save(requestsDto, ownerId);
        log.info("-requestController - addRequest: " + answer);
        return answer;
    }

    @GetMapping
    List<RequestsDto> getOwnRequests(@RequestHeader(HEADER) long ownerId) {
        log.info("+requestController - getOwnRequests: ownerId = " + ownerId);
        List<RequestsDto> answer = requestService.getOwnRequests(ownerId);
        log.info("-requestController - getOwnRequests: " + answer);
        return answer;
    }

    @GetMapping("/all")
    List<RequestsDto> getAllRequest(@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                    @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
                                    @RequestHeader(HEADER) long userId) {
        log.info("+requestController - getAllRequest: from = " + from + ", size = " + size);
        List<RequestsDto> answer = requestService.getAllRequest(from, size, userId);
        log.info("-requestController - getAllRequest: " + answer);
        return answer;
    }

    @GetMapping("/{requestId}")
    RequestsDto getRequestById(@PathVariable long requestId, @RequestHeader(HEADER) long userId) {
        log.info("+requestController - getRequestById: requestId = " + requestId);
        RequestsDto answer = requestService.getRequestById(requestId, userId);
        log.info("-requestController - getRequestById: " + answer);
        return answer;
    }
}
