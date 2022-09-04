package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id; //— уникальный идентификатор бронирования;
    private LocalDateTime start;// — дата и время начала бронирования;
    private LocalDateTime end; //— дата и время конца бронирования;
    private Long itemId;// — вещь, которую пользователь бронирует;
    private Long booker;// — пользователь, который осуществляет бронирование;
    private Booking.Status status;
}
