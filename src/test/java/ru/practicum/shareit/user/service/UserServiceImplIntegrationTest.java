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

    private UserDto actualUserDto;

    @BeforeEach
    public void setUp() {
        actualUserDto = userService.addUser(generator.nextObject(UserDto.class));
    }

    @Test
    @DirtiesContext
    void addUser() {
        assertEquals(1, actualUserDto.getId());
    }

    @Test
    @DirtiesContext
    void getAllUsers() {
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    @DirtiesContext
    void updateUser() {
        actualUserDto = generator.nextObject(UserDto.class);
        actualUserDto.setName("New Name");

        actualUserDto = userService.updateUser(actualUserDto, 1);
        assertEquals("New Name", actualUserDto.getName());
    }

    @Test
    @DirtiesContext
    void getUserById() {
        assertEquals(1, userService.getUserById(actualUserDto.getId()).getId());
    }

    @Test
    @DirtiesContext
    void deleteUser() {
        userService.deleteUser(actualUserDto.getId());
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    @DirtiesContext
    void checkOwner() {
        assertTrue(userService.checkOwner(actualUserDto.getId()));
    }
}