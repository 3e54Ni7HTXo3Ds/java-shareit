package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
    private  final  HashMap <Long, String> emails = new HashMap<>();

    private long getNextUserId() {
        userId++;
        return userId;
    }

    @Override
    public User create(User user) {
        userId = getNextUserId();
        user.setId(userId);
        users.put(userId, user);
        emails.put(userId, user.getEmail());
        return user;

    }

    @Override
    public User update(User user) {
        if (user != null) {
            long updateId = user.getId();
            if (users.containsKey(updateId)) {
                users.put(updateId, user);
                log.info("Обновлен пользователь: {} ", user);
                return user;
            } else {
                userId = getNextUserId();
                user.setId(userId);
                users.put(userId, user);
                log.info("Ранее такого пользователя не было. Добавлен новый пользователь: {} ", user);
            }
        } else
            log.error("Ошибка обновления пользователя: {} ", user);
        return null;
    }

    @Override
    public void delete(User user) {
        if (user != null && users.containsKey(user.getId())) {
            users.remove(user.getId());
            log.info("Удален пользователь: {} ", user);
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
    public User findById(Long id) {
        return users.getOrDefault(id, null);
    }

}
