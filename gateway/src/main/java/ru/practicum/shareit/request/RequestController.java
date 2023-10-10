package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.RequestsDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Min;

@Slf4j
@Validated
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    ResponseEntity<Object> addRequest(@Validated(Create.class) @RequestBody RequestsDto requestsDto, @RequestHeader(HEADER) long ownerId) {
        log.info("+requestController - addRequest: {}", requestsDto);
        ResponseEntity<Object> answer =  requestClient.save(requestsDto, ownerId);
        log.info("-requestController - addRequest: {}", answer);
        return answer;
    }

    @GetMapping
    ResponseEntity<Object> getOwnRequests(@RequestHeader(HEADER) long ownerId) {
        log.info("+requestController - getOwnRequests: ownerId = {}", ownerId);
        ResponseEntity<Object> answer = requestClient.getOwnRequests(ownerId);
        log.info("-requestController - getOwnRequests: {}", answer);
        return answer;
    }

    @GetMapping("/all")
    @Validated
    ResponseEntity<Object> getAllRequest(@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                    @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
                                    @RequestHeader(HEADER) long userId) {
        log.info("+requestController - getAllRequest: from = {}, size = {}", from, size);
        ResponseEntity<Object> answer = requestClient.getAllRequest(from, size, userId);
        log.info("-requestController - getAllRequest: {}", answer);
        return answer;
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getRequestById(@PathVariable long requestId, @RequestHeader(HEADER) long userId) {
        log.info("+requestController - getRequestById: requestId = {}", requestId);
        ResponseEntity<Object> answer = requestClient.getRequestById(requestId, userId);
        log.info("-requestController - getRequestById: {}", answer);
        return answer;
    }
}
