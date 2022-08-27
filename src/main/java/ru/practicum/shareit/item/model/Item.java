package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class Item {
    private Long id; //— уникальный идентификатор вещи;
    private String name;  //— краткое название;
    private String description; //— развёрнутое описание;
    private Boolean available; //— статус о том, доступна или нет вещь для аренды;
    private User owner; //— владелец вещи;
    private Long requestId; /* — если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос.*/

    public Item(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
