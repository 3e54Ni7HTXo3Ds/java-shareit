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
    public User get(@PathVariable Integer id) {
        return userService.get(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws CreatingException, IncorrectParameterException, NotFoundParameterException {
        return userService.create(user);
    }

    @PatchMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int userId) {
        userService.delete(userId);
    }

}
