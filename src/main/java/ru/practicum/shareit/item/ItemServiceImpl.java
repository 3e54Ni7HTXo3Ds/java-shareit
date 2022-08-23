package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;

@Service
@Data
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    public ItemRepository itemRepository;
    public UserService userService;

    @Override
    public Collection<Item> findAll(Long userId) {
        return itemRepository.findAll(userId);
    }

    @Override
    public Item findById(Long itemId) {
        if (itemId > 0) {
            return itemRepository.findById(itemId);
        } else log.error("Некорректный ID: {} ", itemId);
        return null;
    }

    @Override
    public Item create(Long userId, ItemDto itemDto) throws IncorrectParameterException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.findById(userId));
        if (item.getAvailable() == null || item.getName() == null || item.getDescription() == null ||
                item.getName().isBlank() ||
                item.getDescription().isBlank()) {
            log.error("Неверные параметры вещи: {} ", item);
            throw new IncorrectParameterException("Неверные параметры вещи");
        }
        return itemRepository.create(item);
    }

    @Override
    public Item update(Long itemId, Long userId, ItemDto itemDto) throws NotFoundParameterException {
        if (!Objects.equals(userId, findById(itemId).getOwner().getId())){
            throw new NotFoundParameterException("Изменять может только создатель");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.findById(userId));
        return itemRepository.update(itemId, userId, item);
    }

    @Override
    public void delete(Long itemId) {
        itemRepository.delete(itemId);
    }


}
