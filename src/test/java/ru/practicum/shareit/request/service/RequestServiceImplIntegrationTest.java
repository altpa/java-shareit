package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestsDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RequestServiceImplIntegrationTest {
    private static final EasyRandom generator = new EasyRandom();

    @Autowired
    RequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto userDto;
    private RequestsDto requestsDto;

    @BeforeEach
    public void setUp() {
        userDto = userService.addUser(generator.nextObject(UserDto.class));
        requestsDto = requestService.save(generator.nextObject(RequestsDto.class), userDto.getId());
    }

    @Test
    @DirtiesContext
    void save() {
        assertEquals(1, requestsDto.getId());
    }

    @Test
    @DirtiesContext
    void getOwnRequests() {
        List<RequestsDto> requestsDto = requestService.getOwnRequests(userDto.getId());

        assertEquals(1, requestsDto.size());
        assertEquals(1, requestsDto.get(0).getId());
    }

    @Test
    @DirtiesContext
    void getAllRequest() {
        int from = 0;
        int size = 10;

        ItemDto itemDto = generator.nextObject(ItemDto.class);
        itemDto.setRequestId(requestsDto.getId());
        itemService.addItem(itemDto, userDto.getId(), true);

        List<RequestsDto> requestsDto = requestService.getAllRequest(from, size, userDto.getId());

        assertEquals(1, requestsDto.size());
    }

    @Test
    @DirtiesContext
    void getRequestById() {
        assertEquals(1, requestService.getRequestById(requestsDto.getId(), userDto.getId()).getId());
    }
}