package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping //Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
    public ResponseEntity<Object> create(
            @Valid @RequestBody BookingDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) throws IncorrectParameterException {
        log.info("Create booking Dto={}, userId={}", bookingDto, userId);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.error("Неверное время бронирования: {} ", bookingDto);
            throw new IncorrectParameterException("Неверное время бронирования");
        }
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{id}") //Может быть выполнено только владельцем вещи.
    public ResponseEntity<Object> update(@PathVariable("id") Long bookingId,
                                         @RequestParam Boolean approved,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating bookingId {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get bookingId {}, userId={}", bookingId, userId);
        return bookingClient.findByBookingId(bookingId, userId);
    }

    @GetMapping("")
    public ResponseEntity<Object> getByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
            @PositiveOrZero(message = "Offset index must not be less than zero!") @RequestParam(required = false,
                    defaultValue = "0") Integer from,
            @Positive(message = "Limit must not be less than one!") @RequestParam(required = false, defaultValue =
                    "10") Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) throws IncorrectParameterException {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IncorrectParameterException("Unknown state: " + stateParam));
        log.info("Get booking by user with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwnerUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
            @PositiveOrZero(message = "Offset index must not be less than zero!") @RequestParam(required = false,
                    defaultValue = "0") Integer from,
            @Positive(message = "Limit must not be less than one!") @RequestParam(required = false, defaultValue =
                    "10") Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) throws IncorrectParameterException {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IncorrectParameterException("Unknown state: " + stateParam));
        log.info("Get booking by owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getByOwnerUser(userId, state, from, size);
    }
}