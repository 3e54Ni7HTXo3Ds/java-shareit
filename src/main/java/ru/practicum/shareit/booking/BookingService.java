package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;

public interface BookingService {
    Booking create(Long userId, BookingDto bookingDto) throws IncorrectParameterException, NotFoundParameterException;

    Booking update(Long bookingId, Long userId, Boolean approved) throws UpdateException, NotFoundParameterException, IncorrectParameterException;

    Booking findById(Long bookingId) throws NotFoundParameterException;
}
