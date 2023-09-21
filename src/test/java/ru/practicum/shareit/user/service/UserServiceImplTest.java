package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final EasyRandom generator = new EasyRandom();
    private static final UserMapper mapper = UserMapper.INSTANCE;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private Item item1;
    private Request request1;

    @BeforeEach
    public void setUp() {
        user1 = generator.nextObject(User.class);
        request1 = generator.nextObject(Request.class);
        item1 = generator.nextObject(Item.class);
    }

    @Test
    void addUser() {
        when(userRepository.save(any())).thenReturn(user1);

        UserDto answer = userService.addUser(mapper.userToUserDto(user1));

        assertEquals(answer.getId(), user1.getId());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1));

        List<UserDto> answer = userService.getAllUsers();

        assertEquals(1, answer.size());
        assertEquals(answer.get(0).getId(), user1.getId());
    }

    @Test
    void updateUserWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectsDbException.class, () -> {
            UserDto answer = userService.updateUser(mapper.userToUserDto(user1), user1.getId());
        });
    }

    @Test
    void updateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        when(userRepository.save(user1)).thenReturn(user1);

        UserDto answer = userService.updateUser(mapper.userToUserDto(user1), user1.getId());

        assertEquals(answer.getId(), user1.getId());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        UserDto answer = userService.getUserById(user1.getId());

        assertEquals(answer.getId(), user1.getId());
    }

    @Test
    void getUserByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            UserDto answer = userService.getUserById(user1.getId());
        });
    }

    @Test
    void deleteUser() {
        when(userRepository.deleteById(anyLong())).thenReturn(user1);

        UserDto answer = userService.deleteUser(user1.getId());

        assertEquals(answer.getId(), user1.getId());
    }

    @Test
    void checkOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertTrue(userService.checkOwner(user1.getId()));
    }
}