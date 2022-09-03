package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @Column(name = "booking_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //— уникальный идентификатор бронирования;
    @Column(name = "start_date")
    private LocalDateTime start;// — дата и время начала бронирования;
    @Column(name = "end_date")
    private LocalDateTime end; //— дата и время конца бронирования;
    @Column(name = "item_id")
    private Long item;// — вещь, которую пользователь бронирует;
    @Column(name = "booker_id")
    private Long booker;// — пользователь, который осуществляет бронирование;
    @Enumerated(EnumType.STRING)
    private Status status;

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
