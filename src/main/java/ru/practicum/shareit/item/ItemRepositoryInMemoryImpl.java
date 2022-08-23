package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {

    private long itemId;
    private final HashMap<Long, Item> items = new HashMap<>();

    private long getNextItemId() {
        itemId++;
        return itemId;
    }


    @Override
    public Item create(Item item) {
        itemId = getNextItemId();
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item update(Long itemId, Long userId, Item item) {
        if (items.containsKey(itemId)) {
            Item itemModified = items.get(itemId);
            if (item.getName() != null) {
                itemModified.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemModified.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemModified.setAvailable(item.getAvailable());
            }
            items.put(itemId, itemModified);
            log.info("Обновлена вещь: {} ", itemModified);
            return itemModified;
        }
        return null;
    }

    @Override
    public void delete(Long itemId) {
        if (items.containsKey(itemId)) {
            items.remove(itemId);
            log.info("Удалена вещь: {} ", itemId);
        } else {
            throw new ValidationException("Сработала валидация: Такой вещи не существует");
        }
    }

    @Override
    public List<Item> findAll(Long userId) {
        log.info("Общее количество вещей: {} ", items.size());
        return items.values().stream()
                .filter(r -> Objects.equals(r.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(Long itemId) {
        return items.getOrDefault(itemId, null);
    }
}
