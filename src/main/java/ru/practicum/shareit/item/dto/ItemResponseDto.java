package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@Data
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResponseDto {

    private Long id; //— уникальный идентификатор вещи;
    private String name;  //— краткое название;
    private String description; //— развёрнутое описание;
    private Boolean available; //— статус о том, доступна или нет вещь для аренды;
    private Long ownerId; //— владелец вещи;
    private Long requestId;/* — если вещь была создана по запросу другого пользователя, то в этом
      поле будет храниться ссылка на соответствующий запрос.*/
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    @JsonProperty("comments")
    private List<CommentResponseDto1> commentResponseDto;

    public ItemResponseDto(Long id, String name, String description, Boolean available, Long ownerId, Long requestId, BookingDto lastBooking,
                           BookingDto nextBooking, List<CommentResponseDto1> commentResponseDto) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId=ownerId;
        this.requestId = requestId;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.commentResponseDto = commentResponseDto;
    }

}
