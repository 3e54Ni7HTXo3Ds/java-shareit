package ru.practicum.shareit.item;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * // TODO .
 */
@RestController
@Slf4j
@Data
@RequestMapping(path = "/items")
@Component
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ConversionService conversionService;

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException {
        userService.auth(userId);
        return itemService.findAll(userId).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id,
                       @RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException {
        userService.auth(userId);
        return ItemMapper.toItemDto(itemService.findById(id));
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam String text) throws AuthException {
        userService.auth(userId);
        return itemService.search(text).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException, IncorrectParameterException {
        userService.auth(userId);
        return ItemMapper.toItemDto(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable("id") Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) throws CreatingException, AuthException, NotFoundParameterException {
        userService.auth(userId);
        return ItemMapper.toItemDto(itemService.update(itemId, userId, itemDto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException {
        userService.auth(userId);
        itemService.delete(itemId);
    }

}
