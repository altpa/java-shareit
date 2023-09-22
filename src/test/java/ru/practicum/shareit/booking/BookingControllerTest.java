package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private static final EasyRandom generator = new EasyRandom();

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private BookingDto createdBookingDto;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        createdBookingDto = generator.nextObject(BookingDto.class);
        createdBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createdBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        createdBookingDto.setItemId(createdBookingDto.getItem().getId());
        createdBookingDto.setBookerId(createdBookingDto.getBooker().getId());

        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(any(BookingDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdBookingDto;
                });

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createdBookingDto))
                        .header("X-Sharer-User-Id", String.valueOf(createdBookingDto.getBooker().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdBookingDto.getId()));
    }

    @Test
    void changeStatus() throws Exception {
        createdBookingDto.setStatus(APPROVED);
        when(bookingService.changeStatus(anyLong(), anyBoolean(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdBookingDto;
                });

        mvc.perform(patch("/bookings/{bookingId}", createdBookingDto.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", String.valueOf(createdBookingDto.getBooker().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    return createdBookingDto;
                });

        mvc.perform(get("/bookings/{bookingId}", createdBookingDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(createdBookingDto.getBooker().getId()))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdBookingDto.getId()));
    }

    @Test
    void getByUserIdAndStateByBooker() throws Exception {
        when(bookingService.getByUserIdAndStateByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(createdBookingDto);
                });

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(createdBookingDto.getBooker().getId()))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdBookingDto.getId()));
    }

    @Test
    void getByUserIdAndStateByOwner() throws Exception {
        when(bookingService.getByUserIdAndStateByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    return List.of(createdBookingDto);
                });

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", String.valueOf(createdBookingDto.getItem().getOwner().getId()))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdBookingDto.getId()));
    }
}