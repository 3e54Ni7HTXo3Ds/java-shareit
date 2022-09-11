package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto1;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getText());
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