package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private UserService userService;

    private final ConversionService conversionService;

    @Override
    public Collection<ItemDto> findAll(Long userId) {
        return itemRepository.findAll(userId).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(Long itemId) throws IncorrectParameterException {
        if (itemId > 0) {
            return itemRepository.findById(itemId);
        } else log.error("Некорректный ID: {} ", itemId);
        throw new IncorrectParameterException("Некорректный ID");
    }

    @Override
    public Item create(Long userId, ItemDto itemDto) throws IncorrectParameterException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.findById(userId));
        if (
                item.getAvailable() == null || item.getName() == null || item.getDescription() == null ||
                        item.getName().isBlank() ||
                        item.getDescription().isBlank()) {
            log.error("Неверные параметры вещи: {} ", item);
            throw new IncorrectParameterException("Неверные параметры вещи");
        }
        return itemRepository.create(item);
    }

    @Override
    public Item update(Long itemId, Long userId, ItemDto itemDto) throws NotFoundParameterException, IncorrectParameterException, UpdateException {
        if (itemRepository.findById(itemId) == null) {
            log.error("Вещь не найдена: {} ", itemId);
            throw new UpdateException("Вещь не найдена");
        }
        if (!Objects.equals(userId, findById(itemId).getOwner().getId())) {
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

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text.toLowerCase()).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}