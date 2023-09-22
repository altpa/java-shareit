package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final EasyRandom generator = new EasyRandom();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private UserDto createdUserDto1;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        createdUserDto1 = generator.nextObject(UserDto.class);
        createdUserDto1.setEmail("email@mail.com");
    }

    @Test
    void getAllUsers() throws Exception {

        when(userService.getAllUsers())
                .thenAnswer(invocationOnMock -> {
                    return List.of(createdUserDto1);
                });

        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {

        when(userService.getUserById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdUserDto1;
                });

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUserDto1.getId()));
    }

    @Test
    void addUser() throws Exception {
        when(userService.addUser(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    return createdUserDto1;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createdUserDto1))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUserDto1.getId()));
    }

    @Test
    void addUserWhenEmailBlank() throws Exception {
        createdUserDto1.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createdUserDto1))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void addUserWhenEmailFail() throws Exception {
        createdUserDto1.setEmail("mail");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createdUserDto1))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void updateUser() throws Exception {
        createdUserDto1.setId(1);
        createdUserDto1.setName("New Name");

        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdUserDto1;
                });

        mvc.perform(patch("/users/{userId}", String.valueOf(createdUserDto1.getId()))
                        .content(mapper.writeValueAsString(createdUserDto1))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createdUserDto1.getName()));
    }

    @Test
    void deleteUser() throws Exception {

        createdUserDto1.setId(1);
        when(userService.deleteUser(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdUserDto1;
                });

        mvc.perform(delete("/users/{userId}", String.valueOf(createdUserDto1.getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUserDto1.getId()));
    }
}