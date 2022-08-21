package ru.practicum.shareit.user;

import java.util.List;


public interface UserRepository {

    User create(User user);

    User update(User user);

    void delete(User user);

    User findById(Long id);

    List<User> findAll();
}
