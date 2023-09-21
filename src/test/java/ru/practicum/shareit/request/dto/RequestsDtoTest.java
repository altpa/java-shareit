package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestsDtoTest {
    private static final long ID = 1;
    private static final long OWNER_ID = 1;
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String EMAIL = "email@mail.com";

    private static final String NOW = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    private static final String JSON_TO_DESERIALIZE =
            "{\"id\":\""
                    + ID
                    + "\",\"description\":\""
                    + DESCRIPTION
                    + "\",\"created\":\""
                    + NOW
                    + "\",\"items\":"
                        + "["
                            + "{\"id\":\""
                                    + ID
                                    + "\",\"name\":\""
                                    + NAME
                                    + "\",\"description\":\""
                                    + DESCRIPTION
                                    + "\",\"available\":\""
                                    + "true"
                                    + "\",\"owner\":"
                                        + "{\"id\":\""
                                                + ID
                                                + "\",\"name\":\""
                                                + NAME
                                                + "\",\"email\":\""
                                                + EMAIL
                                        + "\"}"
                                    + ",\"requestId\":\""
                                    + ID
                            + "\"}"
                        + "]"
                    + ",\"ownerId\":\""
                    + OWNER_ID
                    + "\"}";

    @Autowired
    private JacksonTester<RequestsDto> tester;

    private RequestsDto requestsDto;

    @BeforeEach
    public void setUp() {
        User owner = new User();
        owner.setId(ID);

        Item item = new Item();
        item.setId(ID);
        item.setName(NAME);
        item.setDescription(DESCRIPTION);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(ID);

        requestsDto = new RequestsDto();
        requestsDto.setId(ID);
        requestsDto.setOwnerId(OWNER_ID);
        requestsDto.setDescription(DESCRIPTION);
        requestsDto.setCreated(LocalDateTime.parse(NOW));
        requestsDto.setItems(Set.of(item));
    }

    @Test
    void requestDtoSerializes() throws IOException {
        JsonContent<RequestsDto> result = tester.write(requestsDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(DESCRIPTION);

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(requestsDto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    @Test
    void requestDtoDeserializes() throws IOException {
        RequestsDto result = tester.parseObject(JSON_TO_DESERIALIZE);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(result.getCreated()).isEqualTo(NOW);
    }
}