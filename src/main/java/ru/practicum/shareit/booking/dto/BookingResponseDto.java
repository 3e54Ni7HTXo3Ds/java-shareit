package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDto {
    private Long id; //— уникальный идентификатор бронирования;
    private Booking.Status status;
    private UserDto booker;// — пользователь, который осуществляет бронирование;
    private ItemDto item;
    private String start;// — дата и время начала бронирования;
    private String end; //— дата и время конца бронирования;
}




