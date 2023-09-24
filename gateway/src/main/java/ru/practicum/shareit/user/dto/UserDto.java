package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;

    private String name;

    @NotBlank(message = "email may not be blank")
    @Email(message = "email must be email")
    private String email;
}
