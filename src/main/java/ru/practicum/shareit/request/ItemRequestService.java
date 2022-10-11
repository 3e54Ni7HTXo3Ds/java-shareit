package ru.practicum.shareit.request;

import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collection;

public interface ItemRequestService {

    Collection<ItemRequestResponseDto> findAll(Long userId) throws NotFoundParameterException,
            IncorrectParameterException;

    Collection<ItemRequestResponseDto> findAllPageble(Long userId, Integer from, Integer size)
            throws NotFoundParameterException,
            IncorrectParameterException;

    ItemRequestResponseDto findByIdDto(Long itemId, Long userId)
            throws IncorrectParameterException, NotFoundParameterException;

    ItemRequestResponseDto create(Long userId, ItemRequestDto dto) throws IncorrectParameterException;


}
