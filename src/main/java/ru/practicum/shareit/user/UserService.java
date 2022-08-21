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
//@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Collection<User> findAll() {
        return userRepository.findAll();
    }


    public User create(User user) throws CreatingException, IncorrectParameterException, NotFoundParameterException {

        if (user != null) {
            String email = user.getEmail();
            if (email==null|| email.isBlank()){
                throw new IncorrectParameterException("");
            }
            List<User> filter = userRepository.findAll().stream()
                    .filter( Objects::nonNull )
                    .filter(r -> Objects.equals(r.getEmail(), email))
                    .limit(1)
                    .collect(Collectors.toList());
            if (filter.isEmpty()) {
                log.info("Добавлен новый пользователь: {} ", user);
                return userRepository.create(user);
            } else {
                log.error("Ошибка создания пользователя: {} ", user);
                throw new CreatingException("");
            }
        } else {
            log.error("Ошибка создания пользователя: {} ", user);
            return null;
        }
    }


    public User update(User user) {
        return null;
    }


    public User get(Integer id) {
        return null;
    }


    public void delete(int userId) {

    }

//    public static void validate(User user) throws IncorrectParameterException, NotFoundParameterException {
//        String message = null;
//        List<User> filter;
//        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || user.getEmail() == null) {
//            message = "электронная почта не может быть пустой и должна содержать символ @";
//            log.error(message);
//            throw new IncorrectParameterException(message);
//        }   else if (user.getId() < 0) {
//            message = "некорректный Id";
//            log.error(message);
//            throw new NotFoundParameterException(message);
//        }
//    }
}
