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
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
    private CommentResponseDto commentResponseDto;

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
        commentResponseDto = new CommentResponseDto(1L, "Comment", 1L, user1.getName(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

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
        when(itemService.findByIdDto(anyLong(), anyLong()))
                .thenReturn(ItemMapper.toItemResponseDto(item));

        mockMvc.perform(get("/items/{id}", item.getId())
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
    void updateItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(ItemMapper.toItemResponseDto(item));

        mockMvc.perform(patch("/items/{id}", item.getId())
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
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void searchItems() throws Exception {
        when(itemService.search(anyString()))
                .thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("text", "text")
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
    void commentItems() throws Exception {
        when(itemService.create(anyLong(), anyLong(), any()))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{id}/comment", item.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText()), String.class))
                .andExpect(jsonPath("$.item", is(commentResponseDto.getItem()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthor()), String.class))
                .andExpect(jsonPath("$.created", is(commentResponseDto.getCreated().toString()), String.class));

    }
}

