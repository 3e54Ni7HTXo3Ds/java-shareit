package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.ItemRepository;
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

public class BookingServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;

    private ItemServiceImpl bookingServiceImpl;
    private User user1;
    private User user2;
    private Booking booking1;
    private Booking booking2;
    private Item item1;
    private Item item2;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingServiceImpl = new ItemServiceImpl(bookingRepository, itemRepository, userRepository);

        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Sam", "1@1.com");
        item1 = new Item(1L, "Ионный трансформатор", "Внеземные технологии", true, user2, null);
        item2 = new Item(2L, "не трансформатор", "земные технологии", false, user1, null);
        booking1 = new Booking(
                1L,
                (LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.SECONDS)),
                (LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.SECONDS)),
                item1,
                user1,
                Booking.Status.WAITING
        );
        booking2 = new Booking(
                2L,
                (LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.SECONDS)),
                (LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.SECONDS)),
                item2,
                user2,
                Booking.Status.WAITING);

        bookingDto1 = BookingMapper.toBookingDto(booking1);
        bookingDto2 = BookingMapper.toBookingDto(booking2);
        bookingResponseDto = BookingMapper.toBookingResponseDto(booking1);

        when(bookingRepository.save(any())).then(invocation -> invocation.getArgument(0));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.ofNullable(item2));
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(3L)).thenReturn(false);

        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.existsById(3L)).thenReturn(false);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking2));
    }

    @Test
    void createPositive() throws NotFoundParameterException, IncorrectParameterException {
        //Act
        var result = bookingServiceImpl.create(user1.getId(), bookingDto1);
        result.setId(booking1.getId());
        //Assert
        assertNotNull(result);
        assertEquals(booking1.getId(), result.getId());
        assertEquals(booking1.getItem(), result.getItem());
        assertEquals(booking1.getStatus(), result.getStatus());
        assertEquals(booking1.getStart(), result.getStart());
        assertEquals(booking1.getEnd(), result.getEnd());
        assertEquals(booking1.getBooker(), result.getBooker());
    }

    @Test
    void createWrongItem() {
        bookingDto2.setItemId(2L);
        final IncorrectParameterException exception4 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));
        assertEquals("Вещь недоступна для бронирования", exception4.getMessage());
    }

    @Test
    void createBookingTime() {
        item2.setAvailable(true);
        // конец раньше сейчас
        bookingDto2.setStart(LocalDateTime.now().minusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
        bookingDto2.setEnd(LocalDateTime.now().minusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
        final IncorrectParameterException exception5 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        // начало раньше сейчас
        bookingDto2.setStart(LocalDateTime.now().minusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
        bookingDto2.setEnd(LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
        final IncorrectParameterException exception6 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        //начало после конца
        bookingDto2.setStart(LocalDateTime.now().plusMinutes(90).truncatedTo(ChronoUnit.SECONDS));
        bookingDto2.setEnd(LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
        final IncorrectParameterException exception7 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        assertEquals("Неверное время бронирования", exception5.getMessage());
        assertEquals("Неверное время бронирования", exception6.getMessage());
        assertEquals("Неверное время бронирования", exception7.getMessage());

    }

    @Test
    void createOwnerNotBooking() {
        item2.setAvailable(true);
        final NotFoundParameterException exception8 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.create(user1.getId(), bookingDto2));
        assertEquals("Владелец не может бронировать", exception8.getMessage());
    }

    @Test
    void updatePositive() throws NotFoundParameterException, IncorrectParameterException, UpdateException {
        //Assign
        Boolean approved = true;
        Boolean rejected = false;

        //Act
        var result1 = bookingServiceImpl.update(booking1.getId(), user2.getId(), approved);
        var result2 = bookingServiceImpl.update(booking2.getId(), user1.getId(), rejected);

        //Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(BookingMapper.toBookingResponseDto(booking1), result1);
        assertEquals(BookingMapper.toBookingResponseDto(booking2), result2);
        assertEquals(BookingMapper.toBookingResponseDto(booking1).getStatus(), Booking.Status.APPROVED);
        assertEquals(BookingMapper.toBookingResponseDto(booking2).getStatus(), Booking.Status.REJECTED);
    }

    @Test
    void updateOnlyOwner() {
        Boolean rejected = false;
        final NotFoundParameterException exception1 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.update(booking1.getId(), user1.getId(), rejected));
        assertEquals("Подтверждать может только создатель", exception1.getMessage());
    }

    @Test
    void updateWrongBooking() {
        Boolean rejected = false;
        booking2.setId(3L);
        final UpdateException exception2 = assertThrows(UpdateException.class,
                () -> bookingServiceImpl.update(booking2.getId(), user2.getId(), rejected));
        assertEquals("Бронирование не найдено", exception2.getMessage());

    }

    @Test
    void updateAfterApprove() {
        Boolean approved = true;
        Boolean rejected = false;
        booking2.setId(2L);
        booking1.setStatus(Booking.Status.APPROVED);
        final IncorrectParameterException exception3 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.update(booking1.getId(), user2.getId(), rejected));
        final IncorrectParameterException exception4 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.update(booking1.getId(), user2.getId(), approved));

        assertEquals("Нельзя изменить после подтверждения", exception3.getMessage());
        assertEquals("Нельзя изменить после подтверждения", exception4.getMessage());
    }


    @Test
    void findByIdPositive() throws NotFoundParameterException {

        var result1 = bookingServiceImpl.findById(booking1.getId(), user2.getId());


        assertNotNull(result1);
        assertEquals(BookingMapper.toBookingResponseDto(booking1), result1);

    }

    @Test
    void findByIdWrongId() {

        booking2.setId(-1L);
        final NotFoundParameterException exception1 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.findById(booking2.getId(), user2.getId()));
        booking2.setId(3L);
        final NotFoundParameterException exception2 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.findById(booking2.getId(), user2.getId()));


        assertEquals("Некорректный ID", exception1.getMessage());
        assertEquals("Некорректный ID", exception2.getMessage());

    }

    @Test
    void findByIdRestrictions() {
        booking2.setId(2L);

        booking2.setBooker(user1);
        final NotFoundParameterException exception3 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.findById(booking2.getId(), user2.getId()));
        booking2.setBooker(user2);
        item2.setOwner(user2);
        final NotFoundParameterException exception4 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.findById(booking2.getId(), user1.getId()));

        assertEquals("Получение данных о конкретном бронировании" +
                " может быть выполнено либо автором бронирования, " +
                "либо владельцем вещи, к которой относится бронирование", exception3.getMessage());
        assertEquals("Получение данных о конкретном бронировании" +
                " может быть выполнено либо автором бронирования, " +
                "либо владельцем вещи, к которой относится бронирование", exception4.getMessage());
    }


    @Test
    void getByUserPositive() throws IncorrectParameterException, NotFoundParameterException {

        when(bookingRepository.findByBookerOrderByStartDesc(any(), any())).thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(),
                any())).thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(any(), any())).thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(any(), any())).thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking1));

        var result1 = bookingServiceImpl.getByUser("ALL", user1.getId(), 1, 1);
        var result2 = bookingServiceImpl.getByUser("CURRENT", user1.getId(), 1, 1);
        var result3 = bookingServiceImpl.getByUser("PAST", user1.getId(), 1, 1);
        var result4 = bookingServiceImpl.getByUser("FUTURE", user1.getId(), 1, 1);
        var result5 = bookingServiceImpl.getByUser("WAITING", user1.getId(), 1, 1);
        var result6 = bookingServiceImpl.getByUser("REJECTED", user1.getId(), 1, 1);


        assertNotNull(result1);
        assertEquals(List.of(bookingResponseDto), result1);
        assertEquals(List.of(bookingResponseDto), result2);
        assertEquals(List.of(bookingResponseDto), result3);
        assertEquals(List.of(bookingResponseDto), result4);
        assertEquals(List.of(bookingResponseDto), result5);
        assertEquals(List.of(bookingResponseDto), result6);
    }

    @Test
    void getByUserNegative() {
        String state = "KEKE";
        final IncorrectParameterException exception3 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.getByUser(state, user1.getId(), 1, 1));

        assertEquals("Unknown state: " + state, exception3.getMessage());
    }

    @Test
    void getByOwnerUserPositive() throws IncorrectParameterException {

        when(bookingRepository.findAllByOwnerPageble(any(), any())).thenReturn(List.of(booking1));
        when(bookingRepository.findCurrentByOwner(any(), any())).thenReturn(List.of(booking1));
        when(bookingRepository.findPastByOwner(any(), any())).thenReturn(List.of(booking1));
        when(bookingRepository.findFutureByOwner(any(), any())).thenReturn(List.of(booking1));
        when(bookingRepository.findWaitingByOwner(any())).thenReturn(List.of(booking1));
        when(bookingRepository.findRejectedByOwner(any())).thenReturn(List.of(booking1));

        var result1 = bookingServiceImpl.getByOwnerUser("ALL", user1.getId(), 1, 1);
        var result2 = bookingServiceImpl.getByOwnerUser("CURRENT", user1.getId(), 1, 1);
        var result3 = bookingServiceImpl.getByOwnerUser("PAST", user1.getId(), 1, 1);
        var result4 = bookingServiceImpl.getByOwnerUser("FUTURE", user1.getId(), 1, 1);
        var result5 = bookingServiceImpl.getByOwnerUser("WAITING", user1.getId(), 1, 1);
        var result6 = bookingServiceImpl.getByOwnerUser("REJECTED", user1.getId(), 1, 1);

        assertNotNull(result1);
        assertEquals(List.of(bookingResponseDto), result1);
        assertEquals(List.of(bookingResponseDto), result2);
        assertEquals(List.of(bookingResponseDto), result3);
        assertEquals(List.of(bookingResponseDto), result4);
        assertEquals(List.of(bookingResponseDto), result5);
        assertEquals(List.of(bookingResponseDto), result6);

    }

    @Test
    void getByOwnerUserNegative() {
        String state = "KEKE";
        final IncorrectParameterException exception3 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.getByOwnerUser(state, user1.getId(), 1, 1));

        assertEquals("Unknown state: " + state, exception3.getMessage());
    }


}
