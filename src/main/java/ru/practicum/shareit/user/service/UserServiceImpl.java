package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectsDbException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao = new UserDao();
    private static final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("+UserServiceImpl - addUser: " + userDto);
        UserDto user = mapper.userToUserDto(userDao.save(mapper.userDtoToUser(userDto)));
        log.info("-UserServiceImpl - addUser: " + user);
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("+UserServiceImpl - getAllUsers");
        List<UserDto> users = userDao.getAll()
                .stream()
                .map(mapper::userToUserDto)
                .collect(toList());
        log.info("-UserServiceImpl - getAllUsers: " + users);
        return users;
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("+UserServiceImpl - addUser: " + userDto + ". userId = " + userId);
        UserDto user = mapper.userToUserDto(userDao.update(mapper.userDtoToUser(userDto), userId));
        log.info("-UserServiceImpl - addUser: " + user);
        return user;
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("+UserServiceImpl - getUserById: userId = " + userId);
        UserDto user =  mapper.userToUserDto(userDao.get(userId).orElseThrow(
                () -> new ObjectsDbException("Юзера с userId = " + userId + " нет")));
        log.info("-UserServiceImpl - getUserById: " + user);
        return user;
    }

    @Override
    public UserDto deleteUser(long userId) {
        log.info("+UserServiceImpl - deleteUser: userId = " + userId);
        UserDto user = mapper.userToUserDto(userDao.delete(userId));
        log.info("-UserServiceImpl - deleteUser: " + user);
        return user;
    }

    @Override
    public void checkOwner(long userId) {
        log.info("+UserServiceImpl - checkOwner: userId = " + userId);
        User owner = userDao.ifExist(userId);
        log.info("-UserServiceImpl - checkOwner: " + owner);
    }
}
