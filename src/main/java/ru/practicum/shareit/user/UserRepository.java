package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface UserRepository {

    User create(User user);
    User update(Long userId, User user);
    void delete(Long userId);
    User findById(Long id);
    List<User> findAll();
}
