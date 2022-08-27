package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {
    Item create(Item item);

    Item update(Long itemId, Long userId, Item item);

    void delete(Long itemId);

    Collection<Item> findAll(Long userId);

    Item findById(Long itemId);

    List<Item> search(String toLowerCase);
}