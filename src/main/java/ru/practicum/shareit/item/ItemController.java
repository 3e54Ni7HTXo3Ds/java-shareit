package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Component
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException {
        userService.auth(userId);
        return itemService.findAll(userId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto get(@PathVariable Long id,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException, IncorrectParameterException, NotFoundParameterException {
        userService.auth(userId);
        return ItemMapper.toItemResponseDto(itemService.findById(id));
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam String text) throws AuthException {
        userService.auth(userId);
        return itemService.search(text);
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
                          @RequestHeader("X-Sharer-User-Id") Long userId) throws CreatingException, AuthException, NotFoundParameterException, IncorrectParameterException, UpdateException {
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
