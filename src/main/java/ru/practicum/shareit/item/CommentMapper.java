package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getText());
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentResponseDto> mapToCommentResponseDto(Iterable<Comment> comments) {
        List<CommentResponseDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponseDto responseDto = toCommentResponseDto(comment);
            dtos.add(responseDto);
        }
        return dtos;
    }
}