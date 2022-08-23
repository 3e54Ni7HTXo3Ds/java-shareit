package ru.practicum.shareit.item;

import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Collection<Item> findAll(Long userId);

    Item findById(Long itemId);

    Item create(Long userId, ItemDto dto) throws IncorrectParameterException;

    Item update(Long itemId, Long userId, ItemDto dto) throws NotFoundParameterException;

    void delete(Long itemId);


}
