package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
class BookingDtoTest {
    private static final long ID = 1;
    private static final String TEXT = "Text";

    private static final String START = LocalDateTime.now().plusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    private static final String END = LocalDateTime.now().plusDays(2)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    private static final BookingStatus STATUS = WAITING;
    private static final long ITEM_ID = 1;
    private static final long BOOKER_ID = 1;

    private static final String JSON_TO_DESERIALIZE =
            "{\"id\":\""
                    + ID
                    + "\",\"start\":\""
                    + START
                    + "\",\"end\":\""
                    + END
                    + "\",\"item\":"
                    + "{"
                    + "}"
                    + ",\"booker\":"
                    + "{"
                    + "}"
                    + ",\"status\":\""
                    + STATUS
                    + "\",\"itemId\":\""
                    + ITEM_ID
                    + "\",\"bookerId\":\""
                    + BOOKER_ID
                    + "\"}";

    @Autowired
    private JacksonTester<BookingDto> tester;

    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setId(ID);
        bookingDto.setStart(LocalDateTime.parse(START));
        bookingDto.setEnd(LocalDateTime.parse(END));
        bookingDto.setStatus(STATUS);
        bookingDto.setItemId(ITEM_ID);
        bookingDto.setBookerId(BOOKER_ID);
    }

    @Test
    void bookingDtoSerializes() throws IOException {
        JsonContent<BookingDto> result = tester.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(START);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(END);
    }

    @Test
    void requestDtoDeserializes() throws IOException {
        BookingDto result = tester.parseObject(JSON_TO_DESERIALIZE);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getStart()).isEqualTo(START);
    }
}