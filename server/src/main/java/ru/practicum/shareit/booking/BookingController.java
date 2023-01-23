package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.user.UserService;

import java.util.List;


@RestController
@Component
@Slf4j
@Data
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping //Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
    public BookingResponseDto create(
            @RequestBody BookingDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, IncorrectParameterException, NotFoundParameterException {
        userService.auth(userId);
        return BookingMapper.toBookingResponseDto(bookingService.create(userId, bookingDto));
    }

    @PatchMapping("/{id}") //Может быть выполнено только владельцем вещи.
    public BookingResponseDto update(@PathVariable("id") Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) throws
            AuthException, NotFoundParameterException, IncorrectParameterException, UpdateException {
        userService.auth(userId);
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{id}")
    public BookingResponseDto getById(@PathVariable("id") Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, NotFoundParameterException {
        userService.auth(userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping("")
    public List<BookingResponseDto> getByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size,
                                              @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, IncorrectParameterException, NotFoundParameterException {
        userService.auth(userId);
        return bookingService.getByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwnerUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, IncorrectParameterException {
        userService.auth(userId);
        return bookingService.getByOwnerUser(state, userId, from, size);
    }
}
