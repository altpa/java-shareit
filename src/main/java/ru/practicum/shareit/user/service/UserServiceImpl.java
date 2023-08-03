package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserException;
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
        log.info("+addUser: " + userDto);
        UserDto user = mapper.userToUserDto(userDao.save(mapper.userDtoToUser(userDto)));
        log.info("-addUser: " + user);
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("+getAllUsers");
        List<UserDto> users = userDao.getAll()
                .stream()
                .map(mapper::userToUserDto)
                .collect(toList());
        log.info("-getAllUsers: " + users);
        return users;
    }

    @Override
    public UserDto updateUser(UserDto userDto, int userId) {
        log.info("+addUser: " + userDto + ". userId = " + userId);
        UserDto user = mapper.userToUserDto(userDao.update(mapper.userDtoToUser(userDto), userId));
        log.info("-addUser: " + user);
        return user;
    }

    @Override
    public UserDto getUserById(int userId) {
        log.info("+getUserById: userId = " + userId);
        UserDto user =  mapper.userToUserDto(userDao.get(userId).orElseThrow(
                () -> new UserException("Юзера с userId = " + userId + " нет")));
        log.info("-getUserById: " + user);
        return user;
    }

    @Override
    public UserDto deleteUser(int userId) {
        log.info("+deleteUser: userId = " + userId);
        UserDto user = mapper.userToUserDto(userDao.delete(userId));
        log.info("-deleteUser: " + user);
        return user;
    }

    @Override
    public void checkOwner(int userId) {
        log.info("+checkOwner: userId = " + userId);
        User owner = userDao.ifExist(userId);
        log.info("-checkOwner: " + owner);
    }
}
