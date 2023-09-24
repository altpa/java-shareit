package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.RequestsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    private static final EasyRandom generator = new EasyRandom();

    private static final RequestMapper mapper = RequestMapper.INSTANCE;

    @Mock
    private  RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private  UserService userService;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User createdUser1;
    private Item createdItem1;
    private Request createdRequest1;

    @BeforeEach
    public void setUp() {
        createdUser1 = generator.nextObject(User.class);
        createdRequest1 = generator.nextObject(Request.class);
        createdItem1 = generator.nextObject(Item.class);
    }

    @Test
    void save() {
        createdRequest1.setOwnerId(createdUser1.getId());
        RequestsDto requestsDto = mapper.requestToRequestDto(createdRequest1);

        when(userService.checkOwner(anyLong())).thenReturn(true);

        when(requestRepository.save(any())).thenReturn(createdRequest1);

        RequestsDto answer = requestService.save(requestsDto, createdUser1.getId());

        assertEquals(answer.getId(), createdRequest1.getId());
    }

    @Test
    void saveWhenUserNotFound() {
        RequestsDto requestsDto = mapper.requestToRequestDto(createdRequest1);

        when(userService.checkOwner(anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            RequestsDto answer = requestService.save(requestsDto, createdUser1.getId());
        });
    }

    @Test
    void getOwnRequests() {
        when(userService.checkOwner(anyLong())).thenReturn(true);

        when(requestRepository.findByOwnerId(anyLong()))
                .thenReturn(Streamable.of(List.of(createdRequest1)));

        when(itemRepository.findByRequestId(anyLong())).thenReturn(Streamable.of(List.of(createdItem1)));

        List<RequestsDto> answer = requestService.getOwnRequests(createdUser1.getId());

        assertEquals(1, answer.size());
        assertEquals(answer.get(0).getId(), createdRequest1.getId());
        assertEquals(1, answer.get(0).getItems().size());
    }

    @Test
    void getOwnRequestsWhenUserNotFound() {
        when(userService.checkOwner(anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            List<RequestsDto> answer = requestService.getOwnRequests(createdUser1.getId());
        });
    }

    @Test
    void getAllRequest() {
        Page<Request> page = new PageImpl<>(List.of(createdRequest1));

        when(userService.checkOwner(anyLong())).thenReturn(true);

        when(requestRepository.findAll((Pageable) any())).thenReturn(page);

        when(itemRepository.findByOwnerIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(Streamable.of(List.of(createdItem1)));

        List<RequestsDto> answer = requestService.getAllRequest(0, 2, createdUser1.getId());

        assertEquals(answer.get(0).getId(), createdRequest1.getId());
    }

    @Test
    void getAllRequestWhenUserNotFound() {
        when(userService.checkOwner(anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            List<RequestsDto> answer = requestService.getAllRequest(0, 2, createdUser1.getId());
        });
    }

    @Test
    void getRequestById() {
        when(userService.checkOwner(anyLong())).thenReturn(true);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(createdRequest1));

        when(itemRepository.findByRequestId(anyLong())).thenReturn(Streamable.of(List.of(createdItem1)));

        RequestsDto answer = requestService.getRequestById(createdRequest1.getId(), createdUser1.getId());

        assertEquals(answer.getId(), createdRequest1.getId());
    }

    @Test
    void getRequestByIdWhenUserNotFound() {
        when(userService.checkOwner(anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            RequestsDto answer = requestService.getRequestById(createdRequest1.getId(), createdUser1.getId());
        });
    }
}