package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
    private long userId;
    private final HashMap<Long, User> users = new HashMap<>();

    private long getNextUserId() {
        userId++;
        return userId;
    }

    @Override
    public User create(User user) {
        userId = getNextUserId();
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        if (users.containsKey(userId)) {
            User userModified = users.get(userId);
            if (user.getName() != null) {
                userModified.setName(user.getName());
            }
            if (user.getEmail() != null) {
                userModified.setEmail(user.getEmail());
            }
            users.put(userId, userModified);
            log.info("Обновлен пользователь: {} ", userModified);
            return userModified;
        }
        return null;
    }

    @Override
    public void delete(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            log.info("Удален пользователь: {} ", userId);
        } else {
            throw new ValidationException("Сработала валидация: Такого пользователя не существует");
        }
    }

    @Override
    public List<User> findAll() {
        log.info("Текущее количество пользователей: {} ", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long userId) {
        return users.getOrDefault(userId, null);
    }

}
