package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto, long userId);

    UserDto getUserById(long userId);

    UserDto deleteUser(long userId);
}