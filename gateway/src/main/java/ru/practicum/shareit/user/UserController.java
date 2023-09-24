package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.debug("+UserController - getAllUsers");
        ResponseEntity<Object> users =  userClient.getAllUsers();
        log.debug("-UserController - getAllUsers: " + users);
        return users;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {
        log.debug("+UserController - getUserById: userId = " + userId);
        ResponseEntity<Object>  user = userClient.getUserById(userId);
        log.debug("-UserController - getUserById: " + user);
        return user;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("+UserController - addUser: " + userDto);
        ResponseEntity<Object>  user = userClient.addUser(userDto);
        log.debug("-UserController - addUser: " + user);
        return user;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        log.debug("+UserController - updateUser: " + userDto + "userId = " + userId);
        ResponseEntity<Object>  user = userClient.updateUser(userDto, userId);
        log.debug("-UserController - updateUser: " + user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        log.debug("+UserController - deleteUser: userId = " + userId);
        ResponseEntity<Object>  user = userClient.deleteUser(userId);
        log.debug("-UserController - deleteUser: " + user);
        return user;
    }
}
