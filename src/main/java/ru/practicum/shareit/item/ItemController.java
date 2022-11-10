package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.error.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Component
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemResponseDto create(@Valid @RequestBody ItemDto itemDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, IncorrectParameterException {
        userService.auth(userId);
        return itemService.create(userId, itemDto);
    }

    @GetMapping
    public Collection<ItemResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, NotFoundParameterException, IncorrectParameterException {
        userService.auth(userId);
        return itemService.findAll(userId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto get(@PathVariable Long id,
                               @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, IncorrectParameterException, NotFoundParameterException {
        userService.auth(userId);
        return itemService.findById(id, userId);
    }

    @PatchMapping("/{id}")
    public ItemResponseDto update(@PathVariable("id") Long itemId,
                                  @RequestBody ItemDto itemDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId)
            throws
            AuthException, NotFoundParameterException, IncorrectParameterException,
            UpdateException {
        userService.auth(userId);
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException {
        userService.auth(userId);
        itemService.delete(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text) throws AuthException {
        userService.auth(userId);
        return itemService.search(text);
    }

    @PostMapping("/{id}/comment")
    public CommentResponseDto create(@Valid @RequestBody CommentDto commentDto,
                                            @PathVariable("id") Long itemId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, IncorrectParameterException, NotFoundParameterException {
        userService.auth(userId);
        return itemService.createComment(userId, itemId, commentDto);
    }


}
