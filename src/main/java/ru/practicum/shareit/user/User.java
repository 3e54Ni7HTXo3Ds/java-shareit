package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;

/**
 * // TODO .
 */

@Data
public class User {

    private Long id; // — уникальный идентификатор пользователя;
    private String name;// — имя или логин пользователя;
    @Email
    private String email;/* — адрес электронной почты (учтите, что два пользователя не могут
            иметь одинаковый адрес электронной почты).*/

}
