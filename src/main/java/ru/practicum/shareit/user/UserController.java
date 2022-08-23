package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ConversionService conversionService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll().stream()
                .map(user -> conversionService.convert(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return UserMapper.toUserDto(userService.findById(id));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) throws CreatingException, IncorrectParameterException {
        return UserMapper.toUserDto(userService.create(userDto));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @Valid @RequestBody UserDto dto) throws CreatingException {
        return UserMapper.toUserDto(userService.update(userId, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long userId) {
        userService.delete(userId);
    }

}
