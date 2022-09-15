package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Data
@Slf4j
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Booking create(Long userId, BookingDto bookingDto) throws IncorrectParameterException,
            NotFoundParameterException {
        Booking booking = BookingMapper.toBooking(bookingDto);
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        booking.setBooker(userRepository.findById(userId).get());
        booking.setStatus(Booking.Status.WAITING);
        Long itemId = booking.getItem().getId();
        if (itemId == null || booking.getStart() == null || booking.getEnd() == null ||
        !itemRepository.existsById(itemId)
        )

        {
            log.error("Неверные параметры бронирования: {} ", booking);
            throw new NotFoundParameterException("Неверные параметры бронирования");
        }
        Item item = itemService.findById(itemId);
        if (!item.getAvailable()) {
            log.error("Вещь недоступна для бронирования: {} ", booking);
            throw new IncorrectParameterException("Вещь недоступна для бронирования");
        }
        if (start.isAfter(end) || start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            log.error("Неверное время бронирования: {} ", booking);
            throw new IncorrectParameterException("Неверное время бронирования");
        }
        if (Objects.equals(item.getOwner().getId(), booking.getBooker().getId())) {
            log.error("Владелец не может бронировать: {} ", booking);
            throw new NotFoundParameterException("Владелец не может бронировать");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public BookingResponseDto update(Long bookingId, Long userId, Boolean approved) throws UpdateException,
            NotFoundParameterException, IncorrectParameterException {
        if (!bookingRepository.existsById(bookingId)) {
            log.error("Бронирование не найдено: {} ", bookingId);
            throw new UpdateException("Бронирование не найдено");
        }
        Item item = findById(bookingId).get().getItem();
        if (!Objects.equals(userId, item.getOwner().getId())) {
            log.error("Подтверждать может только создатель: {} ", bookingId);
            throw new NotFoundParameterException("Подтверждать может только создатель");
        }
        Booking booking = findById(bookingId).get();
        if (approved) {
            if (booking.getStatus() == Booking.Status.APPROVED) {
                log.error("Нельзя изменить после подтверждения: {} ", bookingId);
                throw new IncorrectParameterException("Нельзя изменить после подтверждения");
            }
            booking.setStatus(Booking.Status.APPROVED);
        } else {
            booking.setStatus(Booking.Status.REJECTED);
        }
        bookingRepository.save(booking);
        BookingResponseDto responseDto = BookingMapper.toBookingResponseDto(booking);
        responseDto.getItem().setName(item.getName());
        return responseDto;
    }

    @Override
    public BookingResponseDto findById(Long bookingId, Long userId) throws NotFoundParameterException {
        if (bookingId > 0 && bookingRepository.findById(bookingId).isPresent()) {
            Booking booking = bookingRepository.findById(bookingId).get();
            Item item = itemRepository.findById(booking.getItem().getId()).get();
            if (!(Objects.equals(booking.getBooker().getId(), userId) || Objects.equals(item.getOwner().getId(),
                    userId))) {
                log.error("Получение данных о конкретном бронировании " +
                        " может быть выполнено либо автором бронирования, либо владельцем вещи," +
                        " к которой относится бронирование: {} ", bookingId);
                throw new NotFoundParameterException("Получение данных о конкретном бронировании" +
                        " может быть выполнено либо автором бронирования, " +
                        "либо владельцем вещи, к которой относится бронирование");
            }
            BookingResponseDto responseDto = BookingMapper.toBookingResponseDto(booking);
            responseDto.getItem().setName(item.getName());
            return responseDto;
        } else log.error("Некорректный ID: {} ", bookingId);
        throw new NotFoundParameterException("Некорректный ID");
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public List<BookingResponseDto> getByUser(String state, Long userId)
            throws IncorrectParameterException {
        User user = userRepository.findById(userId).get();
        List<BookingResponseDto> list = null;
        try {
            Booking.State var = Booking.State.valueOf(state);
            switch (var) {
                case ALL:
                    list = BookingMapper.mapToBookingResponseDto(
                            bookingRepository.findByBookerOrderByStartDesc(user));
                    break;
                case CURRENT:
                    list =
                            BookingMapper.mapToBookingResponseDto(
                                    bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user,
                                            LocalDateTime.now(), LocalDateTime.now()));
                    break;
                case PAST:
                    list = BookingMapper.mapToBookingResponseDto(
                            bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(user,
                                    LocalDateTime.now()));
                    break;
                case FUTURE:
                    list = BookingMapper.mapToBookingResponseDto(
                            bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(user,
                                    LocalDateTime.now()));
                    break;
                case WAITING:
                case REJECTED:
                    list = BookingMapper.mapToBookingResponseDto(
                            bookingRepository.findByBookerAndStatusOrderByStartDesc(
                                    user, Booking.Status.valueOf(state)));
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IncorrectParameterException("Unknown state: " + state);
        }
        return list;
    }

    @Override
    public List<BookingResponseDto> getByOwnerUser(String state, Long userId) throws IncorrectParameterException {
        List<BookingResponseDto> list = null;
        try {
            Booking.State var = Booking.State.valueOf(state);
            switch (var) {
                case ALL:
                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findAllByOwner(userId));
                    break;
                case CURRENT:
                    list =
                            BookingMapper.mapToBookingResponseDto(bookingRepository.findCurrentByOwner(userId,
                                    LocalDateTime.now()));
                    break;
                case PAST:
                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findPastByOwner(userId,
                            LocalDateTime.now()));
                    break;
                case FUTURE:
                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findFutureByOwner(userId,
                            LocalDateTime.now()));
                    break;
                case WAITING:

                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findWaitingByOwner(userId));
                    break;
                case REJECTED:
                    list = BookingMapper.mapToBookingResponseDto(bookingRepository.findRejectedByOwner(userId));
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IncorrectParameterException("Unknown state: " + state);
        }
        return list;
    }
}
