package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;

/**
 * // TODO .
 */

@Data
@AllArgsConstructor
public class User {

    private Long id; // — уникальный идентификатор пользователя;
    private String name;// — имя или логин пользователя;
    @Email
    private String email;/* — адрес электронной почты (учтите, что два пользователя не могут
            иметь одинаковый адрес электронной почты).*/

//    public User(Long id, String name, String email) {
//    }
}
