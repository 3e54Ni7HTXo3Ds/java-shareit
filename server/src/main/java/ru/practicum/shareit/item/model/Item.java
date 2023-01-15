package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //— уникальный идентификатор вещи;
    @Column(name = "item_name")
    private String name;  //— краткое название;
    @Column(name = "item_description")
    private String description; //— развёрнутое описание;
    @Column(name = "is_available")
    private Boolean available; //— статус о том, доступна или нет вещь для аренды;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; //— владелец вещи;
    @Column(name = "request_id")
    private Long requestId; /* — если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос.*/

    public Item(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
