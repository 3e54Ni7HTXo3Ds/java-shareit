package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// — уникальный идентификатор запроса;
    @Column(name = "request_description")
    private String description;// — текст запроса, содержащий описание требуемой вещи;
    @Column(name = "requestor_id")
    private Long requestor;// — пользователь, создавший запрос;
    @Transient
    private LocalDateTime created;// — дата и время создания запроса.
}
