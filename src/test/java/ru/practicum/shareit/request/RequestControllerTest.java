package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.RequestsDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private static final EasyRandom generator = new EasyRandom();

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private RequestsDto createdRequestsDto;


    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        createdRequestsDto = new RequestsDto();
        createdRequestsDto.setDescription("Description");
        createdRequestsDto.setId(1);

        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addRequest() throws Exception {
        when(requestService.save(any(RequestsDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdRequestsDto;
                });

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createdRequestsDto))
                        .header("X-Sharer-User-Id", String.valueOf(createdRequestsDto.getOwnerId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdRequestsDto.getId()));

    }

    @Test
    void getOwnRequests() throws Exception {
        createdRequestsDto.setOwnerId(1);
        when(requestService.getOwnRequests(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(createdRequestsDto);
                });

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(createdRequestsDto.getOwnerId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdRequestsDto.getId()));

    }

    @Test
    void getAllRequest() throws Exception {
        createdRequestsDto.setOwnerId(1);
        when(requestService.getAllRequest(anyInt(), anyInt(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(createdRequestsDto);
                });

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(createdRequestsDto.getOwnerId()))
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdRequestsDto.getId()));

    }

    @Test
    void getRequestById() throws Exception {
        createdRequestsDto.setOwnerId(1);
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdRequestsDto;
                });

        mvc.perform(get("/requests/{requestId}", String.valueOf(createdRequestsDto.getId()))
                        .header("X-Sharer-User-Id", String.valueOf(createdRequestsDto.getOwnerId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdRequestsDto.getId()));

    }
}