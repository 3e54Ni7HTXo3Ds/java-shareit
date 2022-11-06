package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;


    private ItemServiceImpl itemServiceImpl;
    private User user1;
    private User user2;
    private Item item1;

    private ItemDto itemDto;
    private Comment comment;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemServiceImpl = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);

        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Sam", "1@1.com");
        item1 = new Item(
                1L,
                "Ионный трансформатор",
                "Внеземные технологии",
                true,
                user2,
                null
        );
        Item item2 = new Item(
                2L,
                "не трансформатор",
                "земные технологии",
                false,
                user1,
                null
        );
        itemResponseDto = ItemMapper.toItemResponseDto(item1);
        itemDto = ItemMapper.toItemDto(item1);

        comment = new Comment(1L, "Comment", item1, user1,
                LocalDateTime.now().minusMinutes(10).truncatedTo(ChronoUnit.SECONDS));
        CommentDto commentDto = new CommentDto(comment.getText());

        when(itemRepository.save(any())).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void findAll() {

        when(itemRepository.findItemByOwnerIdOrderByIdAsc(any())).thenReturn(List.of(item1));

        var result = itemServiceImpl.findAll(user1.getId());

        assertNotNull(result);
        assertEquals(List.of(ItemMapper.toItemResponseDto(item1)), result);

    }

    @Test
    void findById() throws NotFoundParameterException {

        itemResponseDto.setCommentResponseDto(List.of(CommentMapper.toCommentResponseDto(comment)));

        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findByItem(item1)).thenReturn(List.of(comment));

        var result = itemServiceImpl.findById(item1.getId(), user1.getId());

        item1.setId(-1L);
        final NotFoundParameterException exception1 = assertThrows(NotFoundParameterException.class,
                () -> itemServiceImpl.findById(item1.getId(), user2.getId()));


        assertNotNull(result);
        assertEquals(itemResponseDto, result);
        assertEquals("Некорректный ID", exception1.getMessage());

    }

    @Test
    void create() throws IncorrectParameterException {

        var result = itemServiceImpl.create(user2.getId(), itemDto);
        result.setId(1L);

        itemDto.setAvailable(null);
        final IncorrectParameterException exception1 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setAvailable(true);
        itemDto.setName(null);
        final IncorrectParameterException exception2 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setName("thing");
        itemDto.setDescription(null);

        final IncorrectParameterException exception3 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setDescription("desc");
        itemDto.setName(" ");
        final IncorrectParameterException exception4 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setDescription(" ");
        itemDto.setName("thing");
        final IncorrectParameterException exception5 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));

        assertNotNull(result);
        assertEquals(itemResponseDto, result);
        assertEquals("Неверные параметры вещи", exception1.getMessage());
        assertEquals("Неверные параметры вещи", exception2.getMessage());
        assertEquals("Неверные параметры вещи", exception3.getMessage());
        assertEquals("Неверные параметры вещи", exception4.getMessage());
        assertEquals("Неверные параметры вещи", exception5.getMessage());

    }

    @Test
    void update()
            throws IncorrectParameterException, UpdateException {

        var result = itemServiceImpl.create(user2.getId(), itemDto);
        result.setId(1L);

        itemDto.setAvailable(null);
        final IncorrectParameterException exception1 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setAvailable(true);
        itemDto.setName(null);
        final IncorrectParameterException exception2 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setName("thing");
        itemDto.setDescription(null);

        final IncorrectParameterException exception3 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setDescription("desc");
        itemDto.setName(" ");
        final IncorrectParameterException exception4 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));
        itemDto.setDescription(" ");
        itemDto.setName("thing");
        final IncorrectParameterException exception5 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto));

        assertNotNull(result);
        assertEquals(itemResponseDto, result);
        assertEquals("Неверные параметры вещи", exception1.getMessage());
        assertEquals("Неверные параметры вещи", exception2.getMessage());
        assertEquals("Неверные параметры вещи", exception3.getMessage());
        assertEquals("Неверные параметры вещи", exception4.getMessage());
        assertEquals("Неверные параметры вещи", exception5.getMessage());

    }

    @Test
    void delete() {
    }

    @Test
    void search() {
    }

    @Test
    void createComment()
            throws IncorrectParameterException, NotFoundParameterException {


      //  var result = itemServiceImpl.create(user1.getId(), item1.getId(), commentDto);
    }

}
