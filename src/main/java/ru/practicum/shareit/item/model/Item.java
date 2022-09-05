package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
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
    @Column(name = "owner_id")
    private Long owner; //— владелец вещи;
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
