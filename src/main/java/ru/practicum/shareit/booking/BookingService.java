package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking create(Long userId, BookingDto bookingDto) throws IncorrectParameterException, NotFoundParameterException;

    BookingResponseDto update(Long bookingId, Long userId, Boolean approved) throws UpdateException, NotFoundParameterException, IncorrectParameterException;

    BookingResponseDto findById(Long bookingId, Long userId) throws NotFoundParameterException;
    Optional<Booking> findById(Long bookingId) throws NotFoundParameterException;

    List<BookingResponseDto> getByUser(String state, Long userId) throws IncorrectParameterException;

    List<BookingResponseDto> getByOwnerUser(String state, Long userId) throws IncorrectParameterException;
}
