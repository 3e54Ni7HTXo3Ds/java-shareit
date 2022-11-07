package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@Slf4j
@Data
@RequestMapping("/users")
@Component
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) throws IncorrectParameterException, NotFoundParameterException {
        return userService.findById(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) throws
            IncorrectParameterException {
        return UserMapper.toUserDto(userService.create(userDto));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @Valid @RequestBody UserDto dto) throws
            IncorrectParameterException, NotFoundParameterException {
        return UserMapper.toUserDto(userService.update(userId, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long userId) {
        userService.delete(userId);
    }
}
