package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Data
@Slf4j
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final ConversionService conversionService;

    @Override
    public Booking create(Long userId, BookingDto bookingDto) throws IncorrectParameterException, NotFoundParameterException {
        Booking booking = BookingMapper.toBooking(bookingDto);
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        booking.setBooker(userId);
        booking.setStatus(Booking.Status.WAITING);
        if (booking.getItemId() == null || booking.getStart() == null || booking.getEnd() == null ||
                !itemService.itemExists(booking.getItemId())) {
            log.error("Неверные параметры бронирования: {} ", booking);
            throw new NotFoundParameterException("Неверные параметры бронирования");
        }
        if (!itemService.findById(booking.getItemId()).getAvailable()) {
            log.error("Вещь недоступна для бронирования: {} ", booking);
            throw new IncorrectParameterException("Вещь недоступна для бронирования");
        }
        if (start.isAfter(end) || start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            log.error("Неверное время бронирования: {} ", booking);
            throw new IncorrectParameterException("Неверное время бронирования");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking update(Long bookingId, Long userId, Boolean approved) throws UpdateException, NotFoundParameterException, IncorrectParameterException {
        if (!bookingRepository.existsById(bookingId)) {
            log.error("Бронирование не найдено: {} ", bookingId);
            throw new UpdateException("Бронирование не найдено");
        }
        if (!Objects.equals(userId, itemService.findById(findById(bookingId).getItemId()).getOwner())) {
            log.error("Подтверждать может только создатель: {} ", bookingId);
            throw new NotFoundParameterException("Подтверждать может только создатель");
        }
        Booking booking = findById(bookingId);
        if (approved) {
            booking.setStatus(Booking.Status.APPROVED);
        } else {
            booking.setStatus(Booking.Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findById(Long bookingId) throws NotFoundParameterException {
        if (bookingId > 0) {
            if (bookingRepository.findById(bookingId).isPresent()) {
                return bookingRepository.findById(bookingId).get();
            }
        } else log.error("Некорректный ID: {} ", bookingId);
        throw new NotFoundParameterException("Некорректный ID");
    }


}
