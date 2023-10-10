package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private long id;

    @Size(max = 512, groups = {Create.class, Update.class})
    @NotBlank(message = "name may not be blank", groups = Create.class)
    private String name;

    @NotBlank(message = "email may not be blank", groups = Create.class)
    @Email(message = "Email is not valid", regexp = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`" +
            "{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", groups = {Create.class, Update.class})
    @Size(max = 512, groups = {Create.class, Update.class})
    private String email;
}
