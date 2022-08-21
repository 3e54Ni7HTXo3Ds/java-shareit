package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;

import javax.validation.Valid;
import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@Slf4j
@Data
@RequestMapping(path = "/users")
@Component
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws CreatingException, IncorrectParameterException {
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable("id") Long userId, @Valid @RequestBody User user) throws CreatingException {
        return userService.update(userId, user);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long userId) {
        userService.delete(userId);
    }

}
