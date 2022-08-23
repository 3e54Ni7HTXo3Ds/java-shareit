package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */

@Data
public class Booking {

    private Long id; //— уникальный идентификатор бронирования;
    private LocalDateTime start;// — дата и время начала бронирования;
    private LocalDateTime end; //— дата и время конца бронирования;
    private Item item;// — вещь, которую пользователь бронирует;
    private User booker;// — пользователь, который осуществляет бронирование;

    public enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
    /* — статус бронирования. Может принимать одно из значений:
     WAITING — новое бронирование, ожидает одобрения,
     APPROVED — бронирование подтверждено владельцем,
     REJECTED — бронирование отклонено владельцем,
     CANCELED — бронирование отменено создателем.*/
}
