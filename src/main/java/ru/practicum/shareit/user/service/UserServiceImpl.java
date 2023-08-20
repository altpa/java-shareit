package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserRepository userRepository;
    private static final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("+UserServiceImpl - addUser: " + userDto);
        UserDto user = mapper.userToUserDto(userRepository.save(mapper.userDtoToUser(userDto)));
        log.info("-UserServiceImpl - addUser: " + user);
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("+UserServiceImpl - getAllUsers");
        List<UserDto> users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(mapper::userToUserDto)
                .collect(toList());
        log.info("-UserServiceImpl - getAllUsers: " + users);
        return users;
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("+UserServiceImpl - addUser: " + userDto + ". userId = " + userId);

        UserDto user = mapper.userToUserDto(userRepository.findById(userId).orElseThrow(
                () -> new ObjectsDbException("Юзера с userId = " + userId + " нет")));

        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        user.setId(userId);

        user = mapper.userToUserDto(userRepository.save(mapper.userDtoToUser(user)));

        log.info("-UserServiceImpl - addUser: " + user);
        return user;
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("+UserServiceImpl - getUserById: userId = " + userId);
        UserDto user =  mapper.userToUserDto(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Юзера с userId = " + userId + " нет")));
        log.info("-UserServiceImpl - getUserById: " + user);
        return user;
    }

    @Override
    public UserDto deleteUser(long userId) {
        log.info("+UserServiceImpl - deleteUser: userId = " + userId);
        UserDto user = mapper.userToUserDto(userRepository.deleteById(userId));
        log.info("-UserServiceImpl - deleteUser: " + user);
        return user;
    }

    @Override
    public Boolean checkOwner(long userId) {
        log.info("+UserServiceImpl - checkOwner: userId = " + userId);
        Boolean isExist = userRepository.existsById(userId);
        log.info("-UserServiceImpl - checkOwner: userId = " + userId + ", isExist = " + isExist);
        return isExist;
    }
}
