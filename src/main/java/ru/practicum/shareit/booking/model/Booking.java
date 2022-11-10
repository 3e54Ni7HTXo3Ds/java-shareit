package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
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
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

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

    public enum State {
        ALL,
        CURRENT,
        PAST,
        FUTURE,
        WAITING,
        REJECTED
    }
/*
    значения
    CURRENT(англ . «текущие»),
    PAST(англ . «завершённые»),
    FUTURE(англ . «будущие»),
    WAITING(англ . «ожидающие подтверждения»),
    REJECTED(англ . «отклонённые»).
    */

}
