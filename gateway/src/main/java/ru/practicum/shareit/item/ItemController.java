package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long itemId,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating itemId {}, userId={}, itemDto={}", itemId, userId, itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find all items, userId={}", userId);
        return itemClient.findAllItem(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable Long id,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find itemId={}, userId={}", id, userId);
        return itemClient.findByItemId(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Delete itemId {}, userId={}", itemId, userId);
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam String text) {
        log.info("Search, userId={}, text={}", userId, text);
        return itemClient.search(text, userId);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> create(@Valid @RequestBody CommentDto commentDto,
                                         @PathVariable("id") Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Comment itemId {}, userId={}, commentDto={}", itemId, userId, commentDto);
        return itemClient.createComment(userId, itemId, commentDto);
    }


}
