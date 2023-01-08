package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestResponseDto itemRequestResponseDto;
    private UserDto userDto;

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
        itemRequest = new ItemRequest(
                1L, "Desc", user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        itemRequestDto = new ItemRequestDto("Desc");
        itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest);
        userDto = UserMapper.toUserDto(user1);
    }

    @Test
    void createNewItemRequest() throws Exception {
        when(itemRequestService.create(anyLong(), any()))
                .thenReturn(itemRequestResponseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestResponseDto.getCreated().toString()), String.class));
        verify(itemRequestService, Mockito.times(1)).create(userDto.getId(), itemRequestDto);

    }

    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.findAll(anyLong()))
                .thenReturn((List.of(itemRequestResponseDto)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestResponseDto.getCreated().toString()), String.class));
        verify(itemRequestService, Mockito.times(1)).findAll(userDto.getId());
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.findById(anyLong(), anyLong()))
                .thenReturn(itemRequestResponseDto);

        mockMvc.perform(get("/requests/{id}", item.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestResponseDto.getCreated().toString()), String.class));
        verify(itemRequestService, Mockito.times(1)).findById(item.getId(), userDto.getId());
    }

    @Test
    void getAllItemRequestsPageble() throws Exception {
        when(itemRequestService.findAllPageble(anyLong(), anyInt(), anyInt()))
                .thenReturn((List.of(itemRequestResponseDto)));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "1")
                        .param("size", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestResponseDto.getCreated().toString()), String.class));
        verify(itemRequestService, Mockito.times(1)).findAllPageble(userDto.getId(),1,1);
    }

}
