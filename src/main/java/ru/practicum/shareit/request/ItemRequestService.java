package ru.practicum.shareit.request;

import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collection;
import java.util.List;

public interface ItemRequestService {

    Collection<ItemRequestResponseDto> findAll(Long userId) throws NotFoundParameterException,
            IncorrectParameterException;

    Collection<ItemRequestResponseDto> findAllPageble(Long userId, Integer from , Integer size) throws NotFoundParameterException,
            IncorrectParameterException;

    ItemRequestResponseDto findByIdDto(Long itemId, Long userId)
            throws IncorrectParameterException, NotFoundParameterException;

    ItemRequestResponseDto create(Long userId, ItemRequestDto dto) throws IncorrectParameterException;



}
