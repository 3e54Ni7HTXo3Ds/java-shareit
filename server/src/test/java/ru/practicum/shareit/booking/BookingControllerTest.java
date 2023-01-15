package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        User user1 = new User(1L, "John", "john.doe@mail.com");
        User user2 = new User(2L, "Sam", "1@1.com");
        Item item = new Item(
                1L,
                "Ионный трансформатор",
                "Внеземные технологии",
                true,
                user2,
                null
        );
        booking = new Booking(
                1L,
                (LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.SECONDS)),
                (LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.SECONDS)),
                item,
                user1,
                Booking.Status.WAITING
        );

        userDto = UserMapper.toUserDto(user1);
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void createNewBooking() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString()), Booking.Status.class));
        verify(bookingService, Mockito.times(1)).create(userDto.getId(), bookingDto);
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(BookingMapper.toBookingResponseDto(booking));

        mockMvc.perform(patch("/bookings/{id}", booking.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString()), Booking.Status.class));
        verify(bookingService, Mockito.times(1)).update(booking.getId(), userDto.getId(), true);
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(BookingMapper.toBookingResponseDto(booking));

        mockMvc.perform(get("/bookings/{id}", booking.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString()), Booking.Status.class));
        verify(bookingService, Mockito.times(1)).findById(booking.getId(), userDto.getId());
    }

    @Test
    void getBookingsByUser() throws Exception {
        when(bookingService.getByUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(BookingMapper.mapToBookingResponseDto(List.of(booking)));

        mockMvc.perform(get("/bookings", booking.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString()), String.class))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString()), String.class))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString()), Booking.Status.class));
        verify(bookingService, Mockito.times(1)).getByUser("ALL", userDto.getId(), 1, 1);

        mockMvc.perform(get("/bookings", booking.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "-1")
                        .param("size", "-1")
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getByOwnerUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(BookingMapper.mapToBookingResponseDto(List.of(booking)));

        mockMvc.perform(get("/bookings/owner", booking.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString()), String.class))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString()), String.class))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString()), Booking.Status.class));
        verify(bookingService, Mockito.times(1)).getByOwnerUser("ALL", userDto.getId(), 1, 1);

        mockMvc.perform(get("/bookings/owner", booking.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "-1")
                        .param("size", "-1")
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}

