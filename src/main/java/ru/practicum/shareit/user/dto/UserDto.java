package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;
    private String name;
    @NotBlank(message = "email may not be blank", groups = Marker.OnCreate.class)
    @Email(message = "email must be email", groups = Marker.OnCreate.class)
    private String email;
}
