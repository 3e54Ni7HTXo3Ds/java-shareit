package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.CommentResponseDto1;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {


    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                null,
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getText());
    }

    public static List<CommentResponseDto> mapToCommentResponseDto(Iterable<Comment> comments) {
        List<CommentResponseDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponseDto responseDto = toCommentResponseDto(comment);

            dtos.add(responseDto);
        }
        return dtos;
    }

    public static CommentResponseDto1 toCommentResponseDto1(Comment comment) {
        return new CommentResponseDto1(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                null,
                new CommentResponseDto1.UserResponseDto(comment.getAuthor(), null),
                comment.getCreated()
        );
    }

    public static List<CommentResponseDto1> mapToCommentResponseDto1(Iterable<Comment> comments) {
        List<CommentResponseDto1> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponseDto1 responseDto = toCommentResponseDto1(comment);
            dtos.add(responseDto);
        }
        return dtos;
    }
}