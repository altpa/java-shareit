package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private long id;

    @Size(min = 1, max = 255)
    @NotBlank(message = "name may not be blank", groups = Create.class)
    private String name;

    @NotBlank(message = "email may not be blank", groups = Create.class)
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", groups = Create.class)
    @Size(min = 1, max = 512)
    private String email;
}
