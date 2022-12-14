package ru.practicum.shareit.item;

import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Collection<ItemResponseDto> findAll(Long userId) throws NotFoundParameterException, IncorrectParameterException;

    ItemResponseDto findById(Long itemId, Long userId)
            throws IncorrectParameterException, NotFoundParameterException;

    ItemResponseDto create(Long userId, ItemDto dto) throws IncorrectParameterException;

    ItemResponseDto update(Long itemId, Long userId, ItemDto dto)
            throws NotFoundParameterException, IncorrectParameterException, UpdateException;

    void delete(Long itemId);

    List<ItemResponseDto> search(String text);

    CommentResponseDto createComment(Long userId, Long itemId, CommentDto commentDto)
            throws IncorrectParameterException, NotFoundParameterException;
}
