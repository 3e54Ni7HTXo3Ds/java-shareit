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
    private Long id;// уникальный идентификатор комментария;
    private String text;// содержимое комментария;
    private Long item;// вещь, к которой относится комментарий;
    @JsonProperty("authorName")
    private String author;// автор комментария;
    private LocalDateTime created;// дата создания комментария
}