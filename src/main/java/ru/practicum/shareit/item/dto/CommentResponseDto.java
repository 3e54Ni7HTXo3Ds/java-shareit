package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponseDto {

    Long id;// уникальный идентификатор комментария;
    String text;// содержимое комментария;
    Long item;// вещь, к которой относится комментарий;
    @JsonProperty("authorName")
    String author;// автор комментария;
    LocalDateTime created;// дата создания комментария
}