package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id; //— уникальный идентификатор бронирования;
    @NotNull(message = "Неверные параметры бронирования")
    @FutureOrPresent (message = "Неверное время бронирования")
    private LocalDateTime start;// — дата и время начала бронирования;
    @NotNull(message = "Неверные параметры бронирования")
    @Future (message = "Неверное время бронирования")
    private LocalDateTime end; //— дата и время конца бронирования;
    private Long itemId;// — вещь, которую пользователь бронирует;
    private Long bookerId;// — пользователь, который осуществляет бронирование;
}
