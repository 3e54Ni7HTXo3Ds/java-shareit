package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> conversionService.convert(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public User findById(Long userId) throws IncorrectParameterException {
        if (userId > 0) {
            return userRepository.findById(userId);
        } else log.error("Некорректный ID: {} ", userId);
        throw new IncorrectParameterException("Некорректный ID");
    }

    @Override
    public User create(UserDto userDto) throws CreatingException, IncorrectParameterException {
        User user = UserMapper.toUser(userDto);
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            throw new IncorrectParameterException("Email не валидный");
        }
        if (uniqueEmail(user)) {
            log.info("Добавлен новый пользователь: {} ", user);
            return userRepository.create(user);
        } else {
            log.error("Ошибка создания пользователя: {} ", user);
            throw new CreatingException("Такой email уже зарегистрирован");
        }
    }

    @Override
    public User update(Long userId, UserDto userDto) throws CreatingException {
        User user = UserMapper.toUser(userDto);
        if (uniqueEmail(user)) {
            log.info("Обновлен пользователь: {} ", user);
            return userRepository.update(userId, user);
        } else {
            log.error("Ошибка создания пользователя: {} ", user);
            throw new CreatingException("Такой email уже зарегистрирован");
        }
    }

    @Override
    public void delete(Long userId) {
        if (userId > 0) {
            log.info("Удаляем пользователя: {} ", userId);
            userRepository.delete(userId);
        } else log.error("Некорректный ID: {} ", userId);
    }

    @Override
    public Boolean uniqueEmail(User user) {
        String email = user.getEmail();
        return userRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(r -> Objects.equals(r.getEmail(), email))
                .findAny().isEmpty();
    }

    @Override
    public Boolean userExists(Long userId) {
        return userRepository.findAll().stream()
                .anyMatch(r -> Objects.equals(r.getId(), userId));
    }

    @Override
    public void auth(Long userId) throws AuthException {
        if (!userExists(userId)) {
            log.info("Не успешная авторизация пользователя: {} ", userId);
            throw new AuthException("Такого пользователя нет");
        } else
            log.info("Успешная авторизация пользователя: {} ", userId);
    }
}
