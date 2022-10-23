package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    private Item item;
    private UserDto userDto;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        User user1 = new User(1L, "John", "john.doe@mail.com");
        User user2 = new User(2L, "Sam", "1@1.com");
        item = new Item(
                1L,
                "Ионный трансформатор",
                "Внеземные технологии",
                true,
                user2,
                null
        );
        itemDto = ItemMapper.toItemDto(item);
        itemResponseDto = ItemMapper.toItemResponseDto(item);
        itemResponseDto.setId(item.getId());
        userDto = UserMapper.toUserDto(user1);

    }

    @Test
    void createNewItem() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(ItemMapper.toItemResponseDto(item));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(itemResponseDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.getRequestId()), Long.class));
    }

    @Test
    void getAllItems() throws Exception {
        when(itemService.findAll(anyLong()))
                .thenReturn(ItemMapper.mapToItemResponseDto(List.of(item)));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].ownerId", is(itemResponseDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemResponseDto.getRequestId()), Long.class));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.findByIdDto(anyLong(),anyLong()))
                .thenReturn(ItemMapper.toItemResponseDto(item));

        mockMvc.perform(get("/items/{id}",item.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(itemResponseDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.getRequestId()), Long.class));
    }
//
//    @Test
//    void updateBooking() throws Exception {
//        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
//                .thenReturn(BookingMapper.toBookingResponseDto(booking));
//
//        mockMvc.perform(patch("/bookings/{id}", booking.getId())
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .param("approved", String.valueOf(true))
//                        .content(mapper.writeValueAsString(booking))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
//                .andExpect(jsonPath("$.start", is(booking.getStart().toString()), String.class))
//                .andExpect(jsonPath("$.end", is(booking.getEnd().toString()), String.class))
//                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
//                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
//                .andExpect(jsonPath("$.status", is(booking.getStatus().toString()), Booking.Status.class));
//    }
//
//    @Test
//    void getBooking() throws Exception {
//        when(bookingService.findById(anyLong(), anyLong()))
//                .thenReturn(BookingMapper.toBookingResponseDto(booking));
//
//        mockMvc.perform(get("/bookings/{id}", booking.getId())
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .content(mapper.writeValueAsString(booking))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
//                .andExpect(jsonPath("$.start", is(booking.getStart().toString()), String.class))
//                .andExpect(jsonPath("$.end", is(booking.getEnd().toString()), String.class))
//                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
//                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
//                .andExpect(jsonPath("$.status", is(booking.getStatus().toString()), Booking.Status.class));
//    }
//
//    @Test
//    void getBookings() throws Exception {
//        when(bookingService.getByUser(anyString(), anyLong(), anyInt(), anyInt()))
//                .thenReturn(BookingMapper.mapToBookingResponseDto(List.of(booking)));
//
//        mockMvc.perform(get("/bookings", booking.getId())
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .param("from", "1")
//                        .param("size", "1")
//                        .param("state", "ALL")
//                        .content(mapper.writeValueAsString(booking))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
//                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString()), String.class))
//                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString()), String.class))
//                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
//                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
//                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString()), Booking.Status.class));
//    }
//
//    @Test
//    void getBookingsByOwner() throws Exception {
//        when(bookingService.getByOwnerUser(anyString(), anyLong(), anyInt(), anyInt()))
//                .thenReturn(BookingMapper.mapToBookingResponseDto(List.of(booking)));
//
//        mockMvc.perform(get("/bookings/owner", booking.getId())
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .param("from", "1")
//                        .param("size", "1")
//                        .param("state", "ALL")
//                        .content(mapper.writeValueAsString(booking))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
//                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString()), String.class))
//                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString()), String.class))
//                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
//                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
//                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString()), Booking.Status.class));
//    }
}

