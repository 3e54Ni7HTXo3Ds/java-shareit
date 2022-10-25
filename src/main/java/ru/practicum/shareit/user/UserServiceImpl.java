package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exceptions.AuthException;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long userId) throws NotFoundParameterException {
        if (userId > 0) {
            if (userRepository.findById(userId).isPresent()) {
                return UserMapper.toUserDto(userRepository.findById(userId).get());
            }
        } else log.error("Некорректный ID: {} ", userId);
        throw new NotFoundParameterException("Некорректный ID");
    }

    @Override
    public User create(UserDto userDto) throws CreatingException, IncorrectParameterException {
        User user = UserMapper.toUser(userDto);
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            throw new IncorrectParameterException("Email не валидный");
        }
        log.info("Добавлен новый пользователь: {} ", user);
        return userRepository.save(user);
    }

    @Override
    public User update(Long userId, UserDto userDto)
            throws CreatingException, IncorrectParameterException, NotFoundParameterException {
        User userNew = UserMapper.toUser(userDto);
        User user = userRepository.findById(userId).get();
        if (userNew.getEmail() != null) {
            user.setEmail(userNew.getEmail());
        }
        if (userNew.getName() != null) {
            user.setName(userNew.getName());
        }
        log.info("Обновлен пользователь: {} ", user);
        return userRepository.save(user);
    }

    @Override
    public void delete(Long userId) {
        if (userId > 0) {
            if (userRepository.existsById(userId)) {
                log.info("Удаляем пользователя: {} ", userId);
                userRepository.deleteById(userId);
            } else {
                log.info("Нет пользователя: {} ", userId);
            }
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
