package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Long id; //— уникальный идентификатор бронирования;
    private String start;// — дата и время начала бронирования;
    private String end; //— дата и время конца бронирования;
    @JsonProperty("item.id")
    private Long itemId;// — вещь, которую пользователь бронирует;
    @JsonProperty("booker.id")
    private Long booker;// — пользователь, который осуществляет бронирование;
    private Booking.Status status;
    @JsonProperty("item.name")
    private String itemName;
}
