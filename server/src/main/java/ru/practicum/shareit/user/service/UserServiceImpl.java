package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final UserMapper mapper = UserMapper.INSTANCE;
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        log.debug("+UserServiceImpl - addUser: " + userDto);
        UserDto user = mapper.userToUserDto(userRepository.save(mapper.userDtoToUser(userDto)));
        log.debug("-UserServiceImpl - addUser: " + user);
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("+UserServiceImpl - getAllUsers");
        List<UserDto> users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(mapper::userToUserDto)
                .collect(toList());
        log.debug("-UserServiceImpl - getAllUsers: " + users);
        return users;
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.debug("+UserServiceImpl - addUser: " + userDto + ". userId = " + userId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectsDbException("Юзера с userId = " + userId + " нет"));

        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        user.setId(userId);

        user = userRepository.save(user);

        log.debug("-UserServiceImpl - addUser: " + user);
        return mapper.userToUserDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        log.debug("+UserServiceImpl - getUserById: userId = " + userId);
        UserDto user = mapper.userToUserDto(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Юзера с userId = " + userId + " нет")));
        log.debug("-UserServiceImpl - getUserById: " + user);
        return user;
    }

    @Override
    public UserDto deleteUser(long userId) {
        log.debug("+UserServiceImpl - deleteUser: userId = " + userId);
        UserDto user = mapper.userToUserDto(userRepository.deleteById(userId));
        log.debug("-UserServiceImpl - deleteUser: " + user);
        return user;
    }
}
