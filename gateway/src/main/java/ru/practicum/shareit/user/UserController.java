package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Create user, userDto={}", userDto);
        return userClient.createUser(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long userId) {
        log.info("Delete user, userId={}", userId);
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UserDto userDto) {
        log.info("Update user, userId={}, userDto={}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Find all users");
        return userClient.findAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable Long id) {
        log.info("Find user, userId={}", id);
        return userClient.findByUserId(id);
    }
}
