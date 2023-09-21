package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    private static final long ID = 1;
    private static final String NAME = "Name";
    private static final String EMAIL = "email@mail.com";

    private static final String JSON_TO_DESERIALIZE =
            "{\"id\":\""
                    + ID
                    + "\",\"name\":\""
                    + NAME
                    + "\",\"email\":\""
                    + EMAIL
                    + "\"}";

    @Autowired
    private JacksonTester<UserDto> tester;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setId(ID);
        userDto.setName(NAME);
        userDto.setEmail(EMAIL);
    }

    @Test
    void userDtoSerializes() throws IOException {
        JsonContent<UserDto> result = tester.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(NAME);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(EMAIL);
    }

    @Test
    void userDtoDeserializes() throws IOException {
        UserDto result = tester.parseObject(JSON_TO_DESERIALIZE);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }
}