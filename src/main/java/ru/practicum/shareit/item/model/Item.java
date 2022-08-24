package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * // TODO .
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Item {

    private Long id; //— уникальный идентификатор вещи;
    private  String name;  //— краткое название;
    private  String description; //— развёрнутое описание;
    private  Boolean available; //— статус о том, доступна или нет вещь для аренды;
    private User owner; //— владелец вещи;
    private  ItemRequest request; /* — если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос.*/

    public Item(String name, String description, Boolean available, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
