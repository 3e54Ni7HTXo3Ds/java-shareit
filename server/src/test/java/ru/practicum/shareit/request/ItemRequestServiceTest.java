package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;

    private ItemRequestServiceImpl itemRequestServiceImpl;
    private User user1;
    private User user2;
    private UserResponseDto userResponseDto;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;
    private ItemRequestResponseDto itemRequestResponseDto;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestServiceImpl = new ItemRequestServiceImpl(itemRequestRepository, itemRepository);

        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Sam", "1@1.com");

        userResponseDto = UserMapper.toUserResponseDto(user1);
        item1 = new Item(
                1L,
                "Ионный трансформатор",
                "Внеземные технологии",
                true,
                user2,
                1L
        );
        item2 = new Item(
                2L,
                "не трансформатор",
                "земные технологии",
                false,
                user1,
                null
        );

        itemResponseDto = ItemMapper.toItemResponseDto(item1);
        itemDto1 = ItemMapper.toItemDto(item1);
        itemDto2 = ItemMapper.toItemDto(item2);

        itemRequest = new ItemRequest(
                1L, "Desc", user1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest);
        when(itemRequestRepository.save(any())).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void findAll() {

        when(itemRepository.findItemByRequestId(any())).thenReturn(List.of(item1));
        when(itemRepository.findByRequestIdIn(any())).thenReturn(List.of(item1, item2));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(any())).thenReturn(List.of(itemRequest));

        var result = itemRequestServiceImpl.findAll(user1.getId());
        itemRequestResponseDto.setItems(List.of(itemResponseDto));
        itemRequestResponseDto.setRequestor(result.get(0).getRequestor());

        assertNotNull(result);
        assertEquals(List.of(itemRequestResponseDto), result);
    }


    @Test
    void findAllPageble() {

        when(itemRepository.findByRequestIdIn(any())).thenReturn(List.of(item1, item2));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(any(), any())).thenReturn(
                new PageImpl<>(List.of(itemRequest)));

        var result = itemRequestServiceImpl.findAllPageble(itemRequest.getId(), 1, 1);

        itemRequestResponseDto.setItems(List.of(itemResponseDto));
        itemRequestResponseDto.setRequestor(result.get(0).getRequestor());

        assertNotNull(result);
        assertEquals(List.of(itemRequestResponseDto), result);
    }

    @Test
    void findById() throws NotFoundParameterException {
        when(itemRepository.findItemByRequestId(any())).thenReturn(List.of(item1));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(itemRequest));

        var result = itemRequestServiceImpl.findById(itemRequest.getId(), user1.getId());
        result.setId(1L);
        result.setCreated(result.getCreated().truncatedTo(ChronoUnit.SECONDS));
        result.setRequestor(itemRequestResponseDto.getRequestor());
        itemRequestResponseDto.setItems(List.of(itemResponseDto));

        assertNotNull(result);
        assertEquals(itemRequestResponseDto, result);

    }

    @Test
    void create() throws IncorrectParameterException {

        var result = itemRequestServiceImpl.create(user1.getId(), new ItemRequestDto("Desc"));
        result.setId(1L);
        result.setCreated(result.getCreated().truncatedTo(ChronoUnit.SECONDS));
        result.setRequestor(itemRequestResponseDto.getRequestor());

        assertNotNull(result);
        assertEquals(itemRequestResponseDto, result);

    }


}
