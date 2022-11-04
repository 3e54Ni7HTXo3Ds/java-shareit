package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @Mock
    private ItemService itemService;


    private BookingServiceImpl bookingServiceImpl;
    private User user1;
    private User user2;
    private Booking booking1;
    private Booking booking2;
    private Item item1;
    private Item item2;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemService = mock(ItemService.class);
        userRepository = mock(UserRepository.class);
        bookingServiceImpl = new BookingServiceImpl(bookingRepository, itemRepository, itemService, userRepository);

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
        booking2 = new Booking(
                2L,
                (LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.SECONDS)),
                (LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.SECONDS)),
                item2,
                user2,
                Booking.Status.WAITING);

        userDto = UserMapper.toUserDto(user1);
        bookingDto1 = BookingMapper.toBookingDto(booking1);
        bookingDto2 = BookingMapper.toBookingDto(booking2);

        when(bookingRepository.save(any())).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void create() throws NotFoundParameterException, IncorrectParameterException {
        //Assign
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemService.findById(1L)).thenReturn(item1);
        when(itemService.findById(2L)).thenReturn(item2);
        when(itemRepository.existsById(any())).thenReturn(true);

        //Act
        // positive
        var result = bookingServiceImpl.create(user1.getId(), bookingDto1);
        result.setId(booking1.getId());
        Optional<User> optionalUser = userRepository.findById(any());

        //exceptions
        bookingDto2.setItemId(null);
        final NotFoundParameterException exception1 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));
        bookingDto2.setItemId(item2.getId());
        bookingDto2.setStart(null);
        final NotFoundParameterException exception2 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));
        bookingDto2.setStart(bookingDto1.getStart());
        bookingDto2.setEnd(null);
        final NotFoundParameterException exception3 = assertThrows(NotFoundParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        bookingDto2.setEnd(bookingDto1.getEnd());
        bookingDto2.setItemId(2L);
        final IncorrectParameterException exception4 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        item2.setAvailable(true);
        //начало после конца
        bookingDto2.setStart(LocalDateTime.now().plusMinutes(90).truncatedTo(ChronoUnit.SECONDS));
        final IncorrectParameterException exception5 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        // конец раньше сейчас
        bookingDto2.setStart(LocalDateTime.now().minusMinutes(90).truncatedTo(ChronoUnit.SECONDS));
        bookingDto2.setEnd(LocalDateTime.now().minusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
        final IncorrectParameterException exception6 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        // конец раньше сейчас
        bookingDto2.setStart(LocalDateTime.now().minusMinutes(90).truncatedTo(ChronoUnit.SECONDS));
        bookingDto2.setEnd(LocalDateTime.now().minusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
        final IncorrectParameterException exception7 = assertThrows(IncorrectParameterException.class,
                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        //Владелец не может бронировать
//        bookingDto2.setEnd(bookingDto1.getStart());
//        bookingDto2.setStart(bookingDto1.getEnd());
//        bookingDto2.setStart(LocalDateTime.now().minusMinutes(70).truncatedTo(ChronoUnit.SECONDS));
//        final IncorrectParameterException exception7 = assertThrows(IncorrectParameterException.class,
//                () -> bookingServiceImpl.create(user2.getId(), bookingDto2));

        //Assert
        assertNotNull(result);
        assertTrue(true, String.valueOf(optionalUser.isPresent()));
        assertEquals(booking1, result);
        assertEquals("Неверные параметры бронирования", exception1.getMessage());
        assertEquals("Неверные параметры бронирования", exception2.getMessage());
        assertEquals("Неверные параметры бронирования", exception3.getMessage());
        assertEquals("Вещь недоступна для бронирования", exception4.getMessage());
        assertEquals("Неверное время бронирования", exception5.getMessage());
        assertEquals("Неверное время бронирования", exception6.getMessage());
        assertEquals("Неверное время бронирования", exception7.getMessage());
    }

    @Test
    void update() {
    }

//    (Long bookingId, Long userId, Boolean approved) throws UpdateException,
//            NotFoundParameterException, IncorrectParameterException {
//        if (!bookingRepository.existsById(bookingId)) {
//            log.error("Бронирование не найдено: {} ", bookingId);
//            throw new UpdateException("Бронирование не найдено");
//        }
//        Item item = findById(bookingId).get().getItem();
//        if (!Objects.equals(userId, item.getOwner().getId())) {
//            log.error("Подтверждать может только создатель: {} ", bookingId);
//            throw new NotFoundParameterException("Подтверждать может только создатель");
//        }
//        Booking booking = findById(bookingId).get();
//        if (approved) {
//            if (booking.getStatus() == Booking.Status.APPROVED) {
//                log.error("Нельзя изменить после подтверждения: {} ", bookingId);
//                throw new IncorrectParameterException("Нельзя изменить после подтверждения");
//            }
//            booking.setStatus(Booking.Status.APPROVED);
//        } else {
//            booking.setStatus(Booking.Status.REJECTED);
//        }
//        bookingRepository.save(booking);
//        BookingResponseDto responseDto = BookingMapper.toBookingResponseDto(booking);
//        responseDto.getItem().setName(item.getName());
//        return responseDto;
//    }

    @Test
    void findByIdDto() {
    }
//    (Long bookingId, Long userId) throws NotFoundParameterException {
//        if (bookingId > 0 && bookingRepository.findById(bookingId).isPresent()) {
//            Booking booking = bookingRepository.findById(bookingId).get();
//            Item item = itemRepository.findById(booking.getItem().getId()).get();
//            if (!(Objects.equals(booking.getBooker().getId(), userId) || Objects.equals(item.getOwner().getId(),
//                    userId))) {
//                log.error("Получение данных о конкретном бронировании " +
//                        " может быть выполнено либо автором бронирования, либо владельцем вещи," +
//                        " к которой относится бронирование: {} ", bookingId);
//                throw new NotFoundParameterException("Получение данных о конкретном бронировании" +
//                        " может быть выполнено либо автором бронирования, " +
//                        "либо владельцем вещи, к которой относится бронирование");
//            }
//            BookingResponseDto responseDto = BookingMapper.toBookingResponseDto(booking);
//            responseDto.getItem().setName(item.getName());
//            return responseDto;
//        } else log.error("Некорректный ID: {} ", bookingId);
//        throw new NotFoundParameterException("Некорректный ID");
//    }

    @Test
    void findById() {

    }

//    (Long bookingId) {
//        return bookingRepository.findById(bookingId);
//    }

    @Test
    void getByUser() {
    }

//    (String state, Long userId, Integer from, Integer size)
//            throws IncorrectParameterException {
//        User user = userRepository.findById(userId).get();
//        List<BookingResponseDto> list = null;
//        try {
//            OffsetBasedPageRequest pageRequest = null;
//
//            if (from != null && size != null) {
//                pageRequest = new OffsetBasedPageRequest(from, size);
//            }
//            if ((from != null && from < 0) || (size != null && size <= 0)) {
//                log.error("Неверные параметры : {} , {} ", from, size);
//                throw new IncorrectParameterException("Неверные параметры ");
//            }
//
//            Booking.State var = Booking.State.valueOf(state);
//            switch (var) {
//                case ALL:
//                    list = BookingMapper.mapToBookingResponseDto(
//                            bookingRepository.findByBookerOrderByStartDesc(user,
//                                    pageRequest));
//                    break;
//                case CURRENT:
//                    list =
//                            BookingMapper.mapToBookingResponseDto(
//                                    bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
//                                            user,
//                                            LocalDateTime.now(), LocalDateTime.now()));
//                    break;
//                case PAST:
//                    list = BookingMapper.mapToBookingResponseDto(
//                            bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(user,
//                                    LocalDateTime.now()));
//                    break;
//                case FUTURE:
//                    list = BookingMapper.mapToBookingResponseDto(
//                            bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(user,
//                                    LocalDateTime.now()));
//                    break;
//                case WAITING:
//                case REJECTED:
//                    list = BookingMapper.mapToBookingResponseDto(
//                            bookingRepository.findByBookerAndStatusOrderByStartDesc(
//                                    user, Booking.Status.valueOf(state)));
//                    break;
//            }
//        } catch (IllegalArgumentException e) {
//            throw new IncorrectParameterException("Unknown state: " + state);
//        }
//        return list;
//    }


    @Test
    void getByOwnerUser() {
    }

//    (String state, Long userId, Integer from, Integer size)
//            throws IncorrectParameterException {
//        List<BookingResponseDto> list = null;
//        try {
//            OffsetBasedPageRequest pageRequest = null;
//            if (from != null && size != null) {
//                pageRequest = new OffsetBasedPageRequest(from, size);
//            }
//            if ((from != null && from < 0) || (size != null && size <= 0)) {
//                log.error("Неверные параметры : {} , {} ", from, size);
//                throw new IncorrectParameterException("Неверные параметры ");
//            }
//
//            Booking.State var = Booking.State.valueOf(state);
//            switch (var) {
//                case ALL:
//                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findAllByOwner(userId,
//                    pageRequest));
//                    break;
//                case CURRENT:
//                    list =
//                            BookingMapper.mapToBookingResponseDto(bookingRepository.findCurrentByOwner(userId,
//                                    LocalDateTime.now()));
//                    break;
//                case PAST:
//                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findPastByOwner(userId,
//                            LocalDateTime.now()));
//                    break;
//                case FUTURE:
//                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findFutureByOwner(userId,
//                            LocalDateTime.now()));
//                    break;
//                case WAITING:
//
//                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findWaitingByOwner(userId));
//                    break;
//                case REJECTED:
//                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findRejectedByOwner(userId));
//                    break;
//            }
//        } catch (IllegalArgumentException e) {
//            throw new IncorrectParameterException("Unknown state: " + state);
//        }
//        return list;
//    }


}
