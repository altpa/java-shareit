package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    private static final long ID = 1;
    private static final String TEXT = "Text";
    private static final String AUTHOR_NAME = "Author Name";
    private static final String NOW = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    private static final String JSON_TO_DESERIALIZE =
            "{\"id\":\""
                    + ID
                    + "\",\"text\":\""
                    + TEXT
                    + "\",\"item\":"
                    + "{"
                    + "}"
                    + ",\"authorName\":\""
                    + AUTHOR_NAME
                    + "\",\"created\":\""
                    + NOW
                    + "\"}";

    @Autowired
    private JacksonTester<CommentDto> tester;

    private CommentDto createdCommentDto;

    @BeforeEach
    public void setUp() {
        createdCommentDto = new CommentDto();
        createdCommentDto.setId(ID);
        createdCommentDto.setText(TEXT);
        createdCommentDto.setAuthorName(AUTHOR_NAME);
        createdCommentDto.setCreated(LocalDateTime.parse(NOW));
    }

    @Test
    void commentDtoSerializes() throws IOException {
        JsonContent<CommentDto> result = tester.write(createdCommentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(TEXT);

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(createdCommentDto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    @Test
    void requestDtoDeserializes() throws IOException {
        CommentDto result = tester.parseObject(JSON_TO_DESERIALIZE);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreated()).isEqualTo(NOW);
    }
}