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

    private User createdUser1;

    @BeforeEach
    public void setUp() {
        createdUser1 = generator.nextObject(User.class);
    }

    @Test
    void addUser() {
        when(userRepository.save(any())).thenReturn(createdUser1);

        UserDto answer = userService.addUser(mapper.userToUserDto(createdUser1));

        assertEquals(answer.getId(), createdUser1.getId());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(createdUser1));

        List<UserDto> answer = userService.getAllUsers();

        assertEquals(1, answer.size());
        assertEquals(answer.get(0).getId(), createdUser1.getId());
    }

    @Test
    void updateUserWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectsDbException.class, () -> {
            UserDto answer = userService.updateUser(mapper.userToUserDto(createdUser1), createdUser1.getId());
        });
    }

    @Test
    void updateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createdUser1));

        when(userRepository.save(createdUser1)).thenReturn(createdUser1);

        UserDto answer = userService.updateUser(mapper.userToUserDto(createdUser1), createdUser1.getId());

        assertEquals(answer.getId(), createdUser1.getId());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createdUser1));

        UserDto answer = userService.getUserById(createdUser1.getId());

        assertEquals(answer.getId(), createdUser1.getId());
    }

    @Test
    void getUserByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            UserDto answer = userService.getUserById(createdUser1.getId());
        });
    }

    @Test
    void deleteUser() {
        when(userRepository.deleteById(anyLong())).thenReturn(createdUser1);

        UserDto answer = userService.deleteUser(createdUser1.getId());

        assertEquals(answer.getId(), createdUser1.getId());
    }

    @Test
    void checkOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertTrue(userService.checkOwner(createdUser1.getId()));
    }
}