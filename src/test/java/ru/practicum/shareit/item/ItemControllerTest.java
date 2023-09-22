package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private static final EasyRandom generator = new EasyRandom();

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemDto createdItemDto;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        createdItemDto = generator.nextObject(ItemDto.class);
        createdItemDto.setLastBooking(null);
        createdItemDto.setNextBooking(null);

        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyLong(), anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                    return createdItemDto;
                });

        when(userService.checkOwner(anyLong())).thenReturn(true);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createdItemDto))
                        .header("X-Sharer-User-Id", String.valueOf(createdItemDto.getOwner().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdItemDto.getId()));
    }

    @Test
    void getAllItemsByOwnerId() throws Exception {
        when(itemService.getAllItemsByOwnerId(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(createdItemDto);
                });

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(createdItemDto.getOwner().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdItemDto.getId()));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdItemDto;
                });

        when(userService.checkOwner(anyLong())).thenReturn(true);

        mvc.perform(patch("/items/{itemId}", String.valueOf(createdItemDto.getId()))
                        .content(mapper.writeValueAsString(createdItemDto))
                        .header("X-Sharer-User-Id", String.valueOf(createdItemDto.getOwner().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdItemDto.getId()));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdItemDto;
                });

        mvc.perform(get("/items/{itemId}", String.valueOf(createdItemDto.getId()))
                        .header("X-Sharer-User-Id", String.valueOf(createdItemDto.getOwner().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdItemDto.getId()));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems(anyString()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(createdItemDto);
                });

        mvc.perform(get("/items/search")
                        .param("text", createdItemDto.getName())
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdItemDto.getId()));
    }

    @Test
    void addComment() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setText("text");
        comment.setId(1);

        when(itemService.addComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return comment;
                });

        mvc.perform(post("/items/{itemId}/comment", String.valueOf(createdItemDto.getId()))
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", String.valueOf(createdItemDto.getOwner().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()));
    }
}