package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id; //— уникальный идентификатор бронирования;
    @FutureOrPresent
    private LocalDateTime start;// — дата и время начала бронирования;
    @Future
    private LocalDateTime end; //— дата и время конца бронирования;
    private Long itemId;// — вещь, которую пользователь бронирует;
    private Long bookerId;// — пользователь, который осуществляет бронирование;
  //  private Booking.Status status;
}
