package ru.practicum.shareit.user;

import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto findById(Long userId) throws IncorrectParameterException, NotFoundParameterException;

    User create(UserDto userDto) throws CreatingException, IncorrectParameterException;

    User update(Long userId, UserDto userDto) throws CreatingException, IncorrectParameterException, NotFoundParameterException;

    void delete(Long userId);

    Boolean uniqueEmail(User user);

    Boolean userExists(Long userId);

    void auth(Long userId) throws AuthException;
}
