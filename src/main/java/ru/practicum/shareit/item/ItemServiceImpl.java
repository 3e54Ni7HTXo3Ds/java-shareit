package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ConversionService conversionService;

    @Override
    public Collection<ItemDto> findAll(Long userId) {
        return itemRepository.findAll().stream()
                .filter(r -> Objects.equals(r.getOwner(), userId))
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(Long itemId) throws IncorrectParameterException, NotFoundParameterException {
        if (itemId > 0) {
            if (itemRepository.findById(itemId).isPresent()) {
                return itemRepository.findById(itemId).get();
            }
        } else log.error("Некорректный ID: {} ", itemId);
        throw new NotFoundParameterException("Некорректный ID");
    }

    @Override
    public Item create(Long userId, ItemDto itemDto) throws IncorrectParameterException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        if (
                item.getAvailable() == null || item.getName() == null || item.getDescription() == null ||
                        item.getName().isBlank() ||
                        item.getDescription().isBlank()) {
            log.error("Неверные параметры вещи: {} ", item);
            throw new IncorrectParameterException("Неверные параметры вещи");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long itemId, Long userId, ItemDto itemDto) throws NotFoundParameterException, IncorrectParameterException, UpdateException {
        if (!itemRepository.existsById(itemId)) {
            log.error("Вещь не найдена: {} ", itemId);
            throw new UpdateException("Вещь не найдена");
        }
        if (!Objects.equals(userId, findById(itemId).getOwner())) {
            throw new NotFoundParameterException("Изменять может только создатель");
        }
        Item itemNew = ItemMapper.toItem(itemDto);
        Item item = findById(itemId);
        if (itemNew.getDescription() != null) {
            item.setDescription(itemNew.getDescription());
        }
        if (itemNew.getName() != null) {
            item.setName(itemNew.getName());
        }
        if (itemNew.getAvailable() != null) {
            item.setAvailable(itemNew.getAvailable());
        }

        log.info("Обновлена вещь: {} ", item);
        return itemRepository.save(item);
    }

    @Override
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text.toLowerCase()).stream()
                .filter(r -> Objects.equals(r.getAvailable(), true))
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean itemExists(Long itemId) {
        return itemRepository.findAll().stream()
                .anyMatch(r -> Objects.equals(r.getId(), itemId));
    }
}
