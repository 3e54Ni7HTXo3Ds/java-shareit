package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;


@RestController
@Slf4j
@RequiredArgsConstructor
@Component
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final UserService userService;
    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestResponseDto create(
            //@Valid
                                             @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) throws AuthException,
            IncorrectParameterException {
        userService.auth(userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException,
            NotFoundParameterException, IncorrectParameterException {
        userService.auth(userId);
        return itemRequestService.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(
           // @Positive

            @PathVariable Long requestId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId)
            throws AuthException, IncorrectParameterException, NotFoundParameterException {
        userService.auth(userId);
        return itemRequestService.findById(requestId, userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseDto> findAllPageable(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) throws AuthException, NotFoundParameterException, IncorrectParameterException {
        checkParams(from, size);
        userService.auth(userId);
        return itemRequestService.findAllPageble(userId, from, size);
    }


    private void checkParams(Integer from, Integer size) throws IncorrectParameterException {
        if ((from < 0) && (size <= 0)) {
            log.error("Неверные параметры : {} , {} ", from, size);
            throw new IncorrectParameterException("Неверные параметры");
        }
    }
}
