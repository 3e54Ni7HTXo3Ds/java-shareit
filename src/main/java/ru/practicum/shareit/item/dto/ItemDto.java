package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * // TODO .
 */

@Data
public class ItemDto {

    private Long id; //— уникальный идентификатор вещи;
    private String name;  //— краткое название;
    private String description; //— развёрнутое описание;
    private boolean available; //— статус о том, доступна или нет вещь для аренды;
    private User owner; //— владелец вещи;
    private ItemRequest request; /** — если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос.*/

    public ItemDto(String name, String description, boolean available, Long aLong) {
    }
}
