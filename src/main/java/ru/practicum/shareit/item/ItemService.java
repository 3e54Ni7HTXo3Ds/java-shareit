package ru.practicum.shareit.item;

import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Collection<ItemDto> findAll(Long userId);

    Item findById(Long itemId) throws IncorrectParameterException, NotFoundParameterException;

    Item create(Long userId, ItemDto dto) throws IncorrectParameterException;

    Item update(Long itemId, Long userId, ItemDto dto) throws NotFoundParameterException, IncorrectParameterException, UpdateException;

    void delete(Long itemId);

    List<ItemDto> search(String text);
}
