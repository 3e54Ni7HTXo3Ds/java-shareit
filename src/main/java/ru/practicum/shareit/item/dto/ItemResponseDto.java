package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResponseDto {

    private Long id; //— уникальный идентификатор вещи;
    private String name;  //— краткое название;
    private String description; //— развёрнутое описание;
    private Boolean available; //— статус о том, доступна или нет вещь для аренды;
    private User owner; //— владелец вещи;
    private Long requestId;/* — если вещь была создана по запросу другого пользователя, то в этом
      поле будет храниться ссылка на соответствующий запрос.*/
    private Booking lastBooking;
    private Booking nextBooking;

    public ItemResponseDto(Long id, String name, String description, Boolean available, Long requestId, Booking lastBooking,
                           Booking nextBooking) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }

}
