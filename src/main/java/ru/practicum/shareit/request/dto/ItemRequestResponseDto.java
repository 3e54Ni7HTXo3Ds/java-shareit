package ru.practicum.shareit.request.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestResponseDto {

    private Long id;// — уникальный идентификатор запроса;
    private String description;// — текст запроса, содержащий описание требуемой вещи;
    private UserResponseDto requestor;// — пользователь, создавший запрос;
    private LocalDateTime created;// — дата и время создания запроса.
    private List<ItemResponseDto> items;

}
