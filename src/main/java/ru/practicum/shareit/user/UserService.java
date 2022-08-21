package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.CreatingException;
import ru.practicum.shareit.error.exceptions.IncorrectParameterException;
import ru.practicum.shareit.error.exceptions.NotFoundParameterException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class UserService {

    private final UserRepository userRepository;

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public User create(User user) throws CreatingException, IncorrectParameterException {
        if (user != null) {
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
        } else {
            log.error("Ошибка создания пользователя: {} ", user);
            return null;
        }
    }


    public User update(Long userId, User user) throws CreatingException {
        if (user != null) {
            if (uniqueEmail(user)) {
                log.info("Обновлен пользователь: {} ", user);
                return userRepository.update(userId, user);
            } else {
                log.error("Ошибка создания пользователя: {} ", user);
                throw new CreatingException("Такой email уже зарегистрирован");
            }
        } else
            log.error("Ошибка обновления пользователя: {} ", user);
        return null;
    }


    public User findById(Long id) {
        return userRepository.findById(id);
    }

    public void delete(Long userId) {
        if (userId > 0) {
            log.info("Удаляем пользователя: {} ", userId);
            userRepository.delete(userId);
        } else  log.error("Некорректный ID: {} ", userId);
    }

    public boolean uniqueEmail(User user) {
        String email = user.getEmail();
        List<User> filter = userRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(r -> Objects.equals(r.getEmail(), email))
                .limit(1)
                .collect(Collectors.toList());
        return filter.isEmpty();

    }
}
