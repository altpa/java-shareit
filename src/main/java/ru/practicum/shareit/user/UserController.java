package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("+UserController - getAllUsers");
        List<UserDto> users =  userService.getAllUsers();
        log.debug("-UserController - getAllUsers: " + users);
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable int userId) {
        log.debug("+UserController - getUserById: userId = " + userId);
        UserDto user = userService.getUserById(userId);
        log.debug("-UserController - getUserById: " + user);
        return user;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("+UserController - addUser: " + userDto);
        UserDto user = userService.addUser(userDto);
        log.debug("-UserController - addUser: " + user);
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        log.debug("+UserController - updateUser: " + userDto + "userId = " + userId);
        UserDto user = userService.updateUser(userDto, userId);
        log.debug("-UserController - updateUser: " + user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable int userId) {
        log.debug("+UserController - deleteUser: userId = " + userId);
        UserDto user = userService.deleteUser(userId);
        log.debug("-UserController - deleteUser: " + user);
        return user;
    }
}
