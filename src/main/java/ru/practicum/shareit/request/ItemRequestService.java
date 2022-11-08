package ru.practicum.shareit.request;

import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestResponseDto> findAll(Long userId) throws NotFoundParameterException,
            IncorrectParameterException;

    List<ItemRequestResponseDto> findAllPageble(Long userId, Integer from, Integer size)
            throws NotFoundParameterException,
            IncorrectParameterException;

    ItemRequestResponseDto findById(Long itemId, Long userId)
            throws IncorrectParameterException, NotFoundParameterException;

    ItemRequestResponseDto create(Long userId, ItemRequestDto dto) throws IncorrectParameterException;


}
