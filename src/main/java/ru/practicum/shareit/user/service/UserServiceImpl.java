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
    private final UserRepository userRepository;
    private static final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public User addUser(UserDto userDto) {
        log.debug("+UserServiceImpl - addUser: " + userDto);
        User user = userRepository.save(mapper.userDtoToUser(userDto));
        log.debug("-UserServiceImpl - addUser: " + user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("+UserServiceImpl - getAllUsers");
        List<User> users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(toList());
        log.debug("-UserServiceImpl - getAllUsers: " + users);
        return users;
    }

    @Override
    public User updateUser(UserDto userDto, long userId) {
        log.debug("+UserServiceImpl - addUser: " + userDto + ". userId = " + userId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectsDbException("Юзера с userId = " + userId + " нет"));

        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        user.setId(userId);

        user = userRepository.save(user);

        log.debug("-UserServiceImpl - addUser: " + user);
        return user;
    }

    @Override
    public User getUserById(long userId) {
        log.debug("+UserServiceImpl - getUserById: userId = " + userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Юзера с userId = " + userId + " нет"));
        log.debug("-UserServiceImpl - getUserById: " + user);
        return user;
    }

    @Override
    public User deleteUser(long userId) {
        log.debug("+UserServiceImpl - deleteUser: userId = " + userId);
        User user = userRepository.deleteById(userId);
        log.debug("-UserServiceImpl - deleteUser: " + user);
        return user;
    }

    @Override
    public Boolean checkOwner(long userId) {
        log.debug("+UserServiceImpl - checkOwner: userId = " + userId);
        Boolean isExist = userRepository.existsById(userId);
        log.debug("-UserServiceImpl - checkOwner: userId = " + userId + ", isExist = " + isExist);
        return isExist;
    }
}
