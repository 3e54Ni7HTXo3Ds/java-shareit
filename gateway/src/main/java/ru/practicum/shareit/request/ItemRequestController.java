package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create request {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find requests, userId={}", userId);
        return itemRequestClient.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @Positive @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return itemRequestClient.findByRequestId(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllPageable(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero(message = "Offset index must not be less than zero!") @RequestParam(required = false,
                    defaultValue = "0") Integer from,
            @Positive(message = "Limit must not be less than one!") @RequestParam(required = false, defaultValue =
                    "10") Integer size
    ) {
        log.info("Get request by pages, userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAllRequestsPageble(userId, from, size);
    }
}