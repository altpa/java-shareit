package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceImplIntegrationTest {
    private static final EasyRandom generator = new EasyRandom();

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = userService.addUser(generator.nextObject(UserDto.class));
    }

    @Test
    @DirtiesContext
    void addUser() {
        assertEquals(1, userDto.getId());
    }

    @Test
    @DirtiesContext
    void getAllUsers() {
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    @DirtiesContext
    void updateUser() {
        userDto = generator.nextObject(UserDto.class);
        userDto.setName("New Name");

        userDto = userService.updateUser(userDto, 1);
        assertEquals("New Name", userDto.getName());
    }

    @Test
    @DirtiesContext
    void getUserById() {
        assertEquals(1, userService.getUserById(userDto.getId()).getId());
    }

    @Test
    @DirtiesContext
    void deleteUser() {
        userService.deleteUser(userDto.getId());
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    @DirtiesContext
    void checkOwner() {
        assertTrue(userService.checkOwner(userDto.getId()));
    }
}