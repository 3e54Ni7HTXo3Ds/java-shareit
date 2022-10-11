package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
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
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;// — пользователь, создавший запрос;
    @Column(name = "request_createdate")
    private LocalDateTime created;// — дата и время создания запроса.

    public ItemRequest(String description) {
        this.description = description;
    }
}
