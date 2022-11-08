package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private Item item2;
    private Booking booking1;

    private ItemDto itemDto1;
    private Comment comment;
    private CommentDto commentDto;
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
        item2 = new Item(
                2L,
                "не трансформатор",
                "земные технологии",
                false,
                user1,
                null
        );
        booking1 = new Booking(
                1L,
                (LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.SECONDS)),
                (LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.SECONDS)),
                item1,
                user1,
                Booking.Status.WAITING
        );
        itemResponseDto = ItemMapper.toItemResponseDto(item1);
        itemDto1 = ItemMapper.toItemDto(item1);
        ItemDto itemDto2 = ItemMapper.toItemDto(item2);

        comment = new Comment(1L, "Comment", item1, user1,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentDto = new CommentDto(comment.getText());

        when(itemRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any())).then(invocation -> invocation.getArgument(0));
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

        var result = itemServiceImpl.create(user2.getId(), itemDto1);
        result.setId(1L);

        itemDto1.setAvailable(null);
        final IncorrectParameterException exception1 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto1));
        itemDto1.setAvailable(true);
        itemDto1.setName(null);
        final IncorrectParameterException exception2 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto1));
        itemDto1.setName("thing");
        itemDto1.setDescription(null);

        final IncorrectParameterException exception3 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto1));
        itemDto1.setDescription("desc");
        itemDto1.setName(" ");
        final IncorrectParameterException exception4 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto1));
        itemDto1.setDescription(" ");
        itemDto1.setName("thing");
        final IncorrectParameterException exception5 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.create(user2.getId(), itemDto1));

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
            throws IncorrectParameterException, UpdateException, NotFoundParameterException {
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(3L)).thenReturn(false);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));

        var result = itemServiceImpl.update(item1.getId(), user2.getId(), itemDto1);
        itemDto1.setDescription(null);
        itemDto1.setName(null);
        itemDto1.setAvailable(null);
        var result1 = itemServiceImpl.update(item1.getId(), user2.getId(), itemDto1);
        item1.setId(3L);

        final UpdateException exception1 = assertThrows(UpdateException.class,
                () -> itemServiceImpl.update(item1.getId(), user1.getId(), itemDto1));

        item1.setId(1L);
        final NotFoundParameterException exception2 = assertThrows(NotFoundParameterException.class,
                () -> itemServiceImpl.update(item1.getId(), user1.getId(), itemDto1));

        assertNotNull(result);
        assertNotNull(result1);
        assertEquals(itemResponseDto, result);
        assertEquals(itemResponseDto, result1);
        assertEquals("Вещь не найдена", exception1.getMessage());
        assertEquals("Изменять может только создатель", exception2.getMessage());
    }

    @Test
    void delete() {
        //Assign

        //Act
        itemServiceImpl.delete(item1.getId());

        //Assert
        verify(itemRepository, Mockito.times(1)).deleteById(item1.getId());

    }

    @Test
    void search() {
        String text1 = "ионный";
        String text2 = "";
        String text3 = null;

        when(itemRepository.search(text1)).thenReturn(List.of(item1));

        var result = itemServiceImpl.search(text1);
        var result1 = itemServiceImpl.search(text2);
        var result2 = itemServiceImpl.search(text3);

        assertNotNull(result);
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(List.of(itemResponseDto), result);
        assertEquals(new ArrayList<>(), result1);
        assertEquals(new ArrayList<>(), result2);
    }

    @Test
    void createComment()
            throws IncorrectParameterException {
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(2L)).thenReturn(false);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(eq(user1), any())).thenReturn(
                List.of(booking1));
        CommentResponseDto commentResponseDto = CommentMapper.toCommentResponseDto(comment);

        var result = itemServiceImpl.createComment(user1.getId(), item1.getId(), commentDto);

        final IncorrectParameterException exception1 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.createComment(user1.getId(), item1.getId(), new CommentDto("")));

        final IncorrectParameterException exception2 = assertThrows(IncorrectParameterException.class,
                () -> itemServiceImpl.createComment(user1.getId(), item2.getId(), new CommentDto("comment")));

        LocalDateTime time = result.getCreated().truncatedTo(ChronoUnit.SECONDS);
        result.setCreated(time);
        result.setId(1L);


        assertNotNull(result);
        assertEquals((commentResponseDto), result);
        assertEquals("Неверный комментарий", exception1.getMessage());
        assertEquals("Неверные параметры вещи", exception2.getMessage());
    }

    @Test
    void addLastNextBooking() {
        when(bookingRepository.findByItemIdOrderByStartAsc(any())).thenReturn(List.of(booking1));

        var result = itemServiceImpl.addLastNextBooking(item1.getId(),user2.getId(),itemResponseDto);

        assertNotNull(result);
        assertEquals((itemResponseDto), result);
    }

}
